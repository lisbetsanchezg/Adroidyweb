package com.beautyshop.movil.ui

import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.beautyshop.movil.fakes.MainDispatcherRule
import com.beautyshop.movil.fakes.TestEnvironment
import com.beautyshop.movil.ui.auth.AuthViewModel
import com.beautyshop.movil.ui.catalog.CatalogViewModel
import com.beautyshop.movil.ui.catalog.ProductDetailViewModel
import com.beautyshop.movil.util.ApiErrorMapper
import com.beautyshop.movil.util.ProductSearch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.net.ConnectException

@OptIn(ExperimentalCoroutinesApi::class)
class AuthAndCatalogViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val env = TestEnvironment()

    // ---- AuthViewModel ----

    @Test
    fun loginConCredencialesIncorrectasMuestraErrorYNoEntra() = runTest {
        env.auth.register("Ana", "ana@correo.com", "secreto1")
        env.auth.logout()
        val viewModel = AuthViewModel(env.auth)

        viewModel.login("ana@correo.com", "mala")
        assertTrue(viewModel.state.value.isLoading)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(ApiErrorMapper.MSG_INVALID_CREDENTIALS, viewModel.state.value.error)
        assertNull(viewModel.user.value)
    }

    @Test
    fun loginCorrectoLimpiaElErrorYPublicaElUsuario() = runTest {
        env.auth.register("Ana", "ana@correo.com", "secreto1")
        env.auth.logout()
        val viewModel = AuthViewModel(env.auth)
        viewModel.login("ana@correo.com", "mala")
        advanceUntilIdle()

        viewModel.login("ana@correo.com", "secreto1")
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertEquals("Ana", viewModel.user.value?.name)
    }

    @Test
    fun registroInvalidoMuestraElMensajeDeValidacion() = runTest {
        val viewModel = AuthViewModel(env.auth)

        viewModel.register("Ana", "correo-malo", "secreto1")
        advanceUntilIdle()

        assertEquals("Escribe un correo válido, por ejemplo ana@correo.com.", viewModel.state.value.error)
    }

    @Test
    fun sinConexionElLoginMuestraErrorDeRed() = runTest {
        env.api.failure = ConnectException("sin red")
        val viewModel = AuthViewModel(env.auth)

        viewModel.login("ana@correo.com", "secreto1")
        advanceUntilIdle()

        assertEquals(ApiErrorMapper.MSG_NO_CONNECTION, viewModel.state.value.error)
    }

    @Test
    fun cerrarSesionQuitaElUsuario() = runTest {
        val viewModel = AuthViewModel(env.auth)
        viewModel.register("Ana", "ana@correo.com", "secreto1")
        advanceUntilIdle()
        assertNotNull(viewModel.user.value)

        viewModel.logout()

        assertNull(viewModel.user.value)
    }

    @Test
    fun clearErrorBorraElMensaje() = runTest {
        val viewModel = AuthViewModel(env.auth)
        viewModel.login("", "")
        advanceUntilIdle()
        assertNotNull(viewModel.state.value.error)

        viewModel.clearError()

        assertNull(viewModel.state.value.error)
    }

    // ---- CatalogViewModel ----

    private fun catalogViewModel() = CatalogViewModel(env.products, env.cart)

    @Test
    fun alAbrirElCatalogoCargaLosProductos() = runTest {
        env.signIn()
        val viewModel = catalogViewModel()
        assertTrue(viewModel.state.value.isLoading)

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(4, viewModel.state.value.visible.size)
        assertEquals(listOf("Todas", "labios", "ojos", "rostro"), viewModel.state.value.categories)
    }

    @Test
    fun busquedaFiltraLaListaVisible() = runTest {
        val viewModel = catalogViewModel()
        advanceUntilIdle()

        viewModel.onQueryChange("paleta")

        assertEquals(listOf("Paleta de Sombras Nude"), viewModel.state.value.visible.map { it.name })
        assertEquals(4, viewModel.state.value.products.size)
    }

    @Test
    fun busquedaSinCoincidenciasDejaLaListaVacia() = runTest {
        val viewModel = catalogViewModel()
        advanceUntilIdle()

        viewModel.onQueryChange("zapatos")

        assertTrue(viewModel.state.value.visible.isEmpty())
    }

    @Test
    fun filtroDeCategoriaSeCombinaConLaBusqueda() = runTest {
        val viewModel = catalogViewModel()
        advanceUntilIdle()

        viewModel.onCategoryChange("ojos")
        assertEquals(2, viewModel.state.value.visible.size)
        viewModel.onQueryChange("máscara")

        assertEquals(listOf("Máscara de Pestañas Volumen"), viewModel.state.value.visible.map { it.name })

        viewModel.onCategoryChange(ProductSearch.ALL_CATEGORIES)
        viewModel.onQueryChange("")
        assertEquals(4, viewModel.state.value.visible.size)
    }

    @Test
    fun siElServidorFallaElCatalogoMuestraErrorYPuedeReintentar() = runTest {
        env.api.productsFailure = ConnectException("sin red")
        val viewModel = catalogViewModel()
        advanceUntilIdle()

        assertEquals(ApiErrorMapper.MSG_NO_CONNECTION, viewModel.state.value.error)
        assertTrue(viewModel.state.value.products.isEmpty())

        env.api.productsFailure = null
        viewModel.load()
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertEquals(4, viewModel.state.value.products.size)
    }

    @Test
    fun agregarAlCarritoActualizaElCarritoYMuestraMensaje() = runTest {
        env.signIn()
        val viewModel = catalogViewModel()
        advanceUntilIdle()

        viewModel.addToCart(viewModel.state.value.products.first())
        advanceUntilIdle()

        assertEquals("Labial Mate Rojo Pasión se agregó al carrito.", viewModel.state.value.message)
        assertEquals(1, env.cart.cart.value.itemCount)
        viewModel.messageShown()
        assertNull(viewModel.state.value.message)
    }

    @Test
    fun agregarAlCarritoSinStockSuficienteMuestraElError() = runTest {
        env.signIn()
        val viewModel = catalogViewModel()
        advanceUntilIdle()
        val mascara = viewModel.state.value.products.first { it.id == 4L }

        repeat(3) { viewModel.addToCart(mascara) }
        advanceUntilIdle()
        viewModel.addToCart(mascara)
        advanceUntilIdle()

        assertEquals("Solo puedes pedir hasta 3 unidades de este producto.", viewModel.state.value.message)
        assertEquals(3, env.cart.cart.value.itemCount)
    }

    // ---- ProductDetailViewModel ----

    @Test
    fun detalleMuestraElProductoYLimitaLaCantidadAlStock() = runTest {
        env.signIn()
        val viewModel = ProductDetailViewModel(4, env.products, env.cart)
        advanceUntilIdle()

        assertEquals("Máscara de Pestañas Volumen", viewModel.state.value.product?.name)
        repeat(10) { viewModel.increase() }
        assertEquals(3, viewModel.state.value.quantity)
        repeat(10) { viewModel.decrease() }
        assertEquals(1, viewModel.state.value.quantity)
    }

    @Test
    fun detalleDeProductoInexistenteMuestraError() = runTest {
        val viewModel = ProductDetailViewModel(99, env.products, env.cart)
        advanceUntilIdle()

        assertEquals("No se encontró el producto.", viewModel.state.value.error)
    }

    @Test
    fun detalleAgregaLaCantidadElegidaAlCarrito() = runTest {
        env.signIn()
        val viewModel = ProductDetailViewModel(1, env.products, env.cart)
        advanceUntilIdle()
        viewModel.increase()
        viewModel.increase()

        viewModel.addToCart()
        advanceUntilIdle()

        assertEquals(3, env.cart.cart.value.itemCount)
        assertNotNull(viewModel.state.value.message)
    }
}
