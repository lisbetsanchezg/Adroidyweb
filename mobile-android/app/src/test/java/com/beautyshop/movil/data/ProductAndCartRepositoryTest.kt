package com.beautyshop.movil.data

import com.beautyshop.movil.data.repository.CartRepository
import com.beautyshop.movil.fakes.TestEnvironment
import com.beautyshop.movil.util.ApiErrorMapper
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.SocketTimeoutException

class ProductAndCartRepositoryTest {

    private val env = TestEnvironment()

    // ---- Catálogo ----

    @Test
    fun catalogoConvierteLosDtoAModelosYLosGuardaEnCache() = runTest {
        val list = env.products.getProducts().getOrThrow()

        assertEquals(4, list.size)
        assertEquals("Labial Mate Rojo Pasión", list.first().name)
        assertEquals(25900.0, list.first().price, 0.001)
        assertEquals("Base Líquida Natural", env.products.findCached(2)?.name)
        assertNull(env.products.findCached(99))
    }

    @Test
    fun catalogoConErrorDeRedDevuelveMensajeYNoBorraLaCache() = runTest {
        env.products.getProducts()
        env.api.failure = SocketTimeoutException("lento")

        val result = env.products.getProducts()

        assertEquals(ApiErrorMapper.MSG_TIMEOUT, result.exceptionOrNull()?.message)
        assertEquals("Labial Mate Rojo Pasión", env.products.findCached(1)?.name)
    }

    // ---- Carrito ----

    @Test
    fun carritoSinSesionPideIniciarSesion() = runTest {
        val result = env.cart.add(1)

        assertEquals("Inicia sesión para usar el carrito.", result.exceptionOrNull()?.message)
    }

    @Test
    fun agregarProductoMuestraNombreYCalculaElTotal() = runTest {
        env.signIn()

        env.cart.add(1, 2)
        val cart = env.cart.add(2, 1).getOrThrow()

        assertEquals(listOf("Labial Mate Rojo Pasión", "Base Líquida Natural"), cart.lines.map { it.productName })
        assertEquals(2 * 25900.0 + 45000.0, cart.total, 0.001)
        assertEquals(3, cart.itemCount)
        assertEquals(cart, env.cart.cart.value)
    }

    @Test
    fun agregarElMismoProductoSumaLaCantidad() = runTest {
        env.signIn()

        env.cart.add(1)
        val cart = env.cart.add(1, 2).getOrThrow()

        assertEquals(1, cart.lines.size)
        assertEquals(3, cart.lines.first().quantity)
    }

    @Test
    fun cambiarCantidadActualizaTotal() = runTest {
        env.signIn()
        env.cart.add(1)

        val cart = env.cart.setQuantity(1, 5).getOrThrow()

        assertEquals(5, cart.lines.first().quantity)
        assertEquals(5 * 25900.0, cart.total, 0.001)
    }

    @Test
    fun cantidadMenorQueUnoSeRechazaSinLlamarAlServidor() = runTest {
        env.signIn()
        env.cart.add(1)
        env.api.calls.clear()

        val result = env.cart.setQuantity(1, 0)

        assertEquals("La cantidad mínima es 1.", result.exceptionOrNull()?.message)
        assertTrue(env.api.calls.none { it == "updateQuantity" })
        assertEquals(1, env.cart.cart.value.lines.first().quantity)
    }

    @Test
    fun cantidadMayorQueElStockSeRechaza() = runTest {
        env.signIn()
        env.cart.add(4) // el producto 4 tiene 3 unidades en stock

        val result = env.cart.setQuantity(4, 4)

        assertEquals("Solo puedes pedir hasta 3 unidades de este producto.", result.exceptionOrNull()?.message)
        assertEquals(1, env.cart.cart.value.lines.first().quantity)
    }

    @Test
    fun agregarMasDelStockDisponibleSeRechazaConLoQueYaHayEnElCarrito() = runTest {
        env.signIn()
        env.cart.add(4, 2)

        val result = env.cart.add(4, 2)

        assertTrue(result.isFailure)
        assertEquals(2, env.cart.cart.value.itemCount)
    }

    @Test
    fun cantidadMaximaNuncaPasaDeNoventaYNueve() = runTest {
        env.signIn()
        env.products.getProducts()

        assertEquals(40, env.cart.maxQuantityFor(1))
        assertEquals(3, env.cart.maxQuantityFor(4))
        assertEquals(CartRepository.MAX_QUANTITY, env.cart.maxQuantityFor(999))
    }

    @Test
    fun quitarProductoLoEliminaDelCarrito() = runTest {
        env.signIn()
        env.cart.add(1)
        env.cart.add(2)

        val cart = env.cart.remove(1).getOrThrow()

        assertEquals(listOf(2L), cart.lines.map { it.productId })
        assertEquals(45000.0, cart.total, 0.001)
    }

    @Test
    fun quitarProductoQueNoEstaMuestraElMensajeDelServidor() = runTest {
        env.signIn()

        val result = env.cart.remove(3)

        assertEquals("El producto no esta en el carrito.", result.exceptionOrNull()?.message)
    }

    @Test
    fun errorDeRedAlAgregarNoCambiaElCarrito() = runTest {
        env.signIn()
        env.cart.add(1)
        env.api.failure = java.net.ConnectException("sin red")

        val result = env.cart.add(2)

        assertEquals(ApiErrorMapper.MSG_NO_CONNECTION, result.exceptionOrNull()?.message)
        assertEquals(1, env.cart.cart.value.lines.size)
    }
}
