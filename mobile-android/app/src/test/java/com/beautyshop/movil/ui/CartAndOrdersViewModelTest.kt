package com.beautyshop.movil.ui

import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.beautyshop.movil.fakes.MainDispatcherRule
import com.beautyshop.movil.fakes.TestEnvironment
import com.beautyshop.movil.ui.cart.CartViewModel
import com.beautyshop.movil.ui.orders.OrderDetailViewModel
import com.beautyshop.movil.ui.orders.OrderHistoryViewModel
import com.beautyshop.movil.util.ApiErrorMapper
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.net.ConnectException

@OptIn(ExperimentalCoroutinesApi::class)
class CartAndOrdersViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val env = TestEnvironment()

    private fun cartViewModel() = CartViewModel(env.cart, env.orders)

    // ---- CartViewModel ----

    @Test
    fun cargarElCarritoMuestraLasLineasYElTotal() = runTest {
        env.signIn()
        env.cart.add(1, 2)
        env.cart.add(2)
        val viewModel = cartViewModel()

        viewModel.load()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(2, viewModel.cart.value.lines.size)
        assertEquals(96800.0, viewModel.cart.value.total, 0.001)
    }

    @Test
    fun aumentarYDisminuirCambiaLaCantidadYElTotal() = runTest {
        env.signIn()
        env.cart.add(1)
        val viewModel = cartViewModel()
        viewModel.load()
        advanceUntilIdle()

        viewModel.increase(viewModel.cart.value.lines.first())
        advanceUntilIdle()
        assertEquals(2, viewModel.cart.value.lines.first().quantity)
        assertEquals(51800.0, viewModel.cart.value.total, 0.001)

        viewModel.decrease(viewModel.cart.value.lines.first())
        advanceUntilIdle()
        assertEquals(1, viewModel.cart.value.lines.first().quantity)
    }

    @Test
    fun disminuirPorDebajoDeUnoMuestraMensajeYConservaLaLinea() = runTest {
        env.signIn()
        env.cart.add(1)
        val viewModel = cartViewModel()
        viewModel.load()
        advanceUntilIdle()

        viewModel.decrease(viewModel.cart.value.lines.first())
        advanceUntilIdle()

        assertEquals("La cantidad mínima es 1.", viewModel.state.value.message)
        assertEquals(1, viewModel.cart.value.lines.first().quantity)
    }

    @Test
    fun aumentarMasDelStockMuestraMensajeYConservaLaCantidad() = runTest {
        env.signIn()
        env.cart.add(4, 3)
        val viewModel = cartViewModel()
        viewModel.load()
        advanceUntilIdle()

        viewModel.increase(viewModel.cart.value.lines.first())
        advanceUntilIdle()

        assertEquals("Solo puedes pedir hasta 3 unidades de este producto.", viewModel.state.value.message)
        assertEquals(3, viewModel.cart.value.lines.first().quantity)
    }

    @Test
    fun quitarUnProductoLoSacaDelCarrito() = runTest {
        env.signIn()
        env.cart.add(1)
        env.cart.add(2)
        val viewModel = cartViewModel()
        viewModel.load()
        advanceUntilIdle()

        viewModel.remove(viewModel.cart.value.lines.first())
        advanceUntilIdle()

        assertEquals(listOf(2L), viewModel.cart.value.lines.map { it.productId })
    }

    @Test
    fun siNoHayConexionElCarritoMuestraErrorYPuedeReintentar() = runTest {
        env.signIn()
        env.api.failure = ConnectException("sin red")
        val viewModel = cartViewModel()

        viewModel.load()
        advanceUntilIdle()
        assertEquals(ApiErrorMapper.MSG_NO_CONNECTION, viewModel.state.value.error)

        env.api.failure = null
        viewModel.load()
        advanceUntilIdle()
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun crearPedidoVaciaElCarritoYPublicaElNumeroDePedido() = runTest {
        env.signIn()
        env.cart.add(1, 2)
        val viewModel = cartViewModel()
        viewModel.load()
        advanceUntilIdle()

        viewModel.checkout()
        assertTrue(viewModel.state.value.isPlacingOrder)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isPlacingOrder)
        assertEquals(1L, viewModel.state.value.createdOrderId)
        assertEquals("Pedido #1 creado correctamente.", viewModel.state.value.message)
        assertTrue(viewModel.cart.value.isEmpty)
        assertEquals(0.0, viewModel.cart.value.total, 0.001)

        viewModel.orderOpened()
        assertNull(viewModel.state.value.createdOrderId)
    }

    @Test
    fun crearPedidoConCarritoVacioMuestraErrorSinNavegar() = runTest {
        env.signIn()
        val viewModel = cartViewModel()
        viewModel.load()
        advanceUntilIdle()

        viewModel.checkout()
        advanceUntilIdle()

        assertNull(viewModel.state.value.createdOrderId)
        assertEquals("Tu carrito está vacío. Agrega productos antes de hacer el pedido.", viewModel.state.value.message)
    }

    @Test
    fun siFallaLaRedAlCrearPedidoElCarritoNoSeVacia() = runTest {
        env.signIn()
        env.cart.add(1)
        val viewModel = cartViewModel()
        viewModel.load()
        advanceUntilIdle()
        env.api.failure = ConnectException("sin red")

        viewModel.checkout()
        advanceUntilIdle()

        assertEquals(ApiErrorMapper.MSG_NO_CONNECTION, viewModel.state.value.message)
        assertEquals(1, viewModel.cart.value.itemCount)
        assertNull(viewModel.state.value.createdOrderId)
    }

    // ---- Pedidos ----

    @Test
    fun historialMuestraLosPedidosMasRecientesPrimero() = runTest {
        env.signIn()
        env.cart.add(1)
        env.orders.createOrder()
        env.cart.add(2)
        env.orders.createOrder()
        val viewModel = OrderHistoryViewModel(env.orders)

        viewModel.load()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(listOf(2L, 1L), viewModel.state.value.orders.map { it.id })
    }

    @Test
    fun historialSinPedidosQuedaVacio() = runTest {
        env.signIn()
        val viewModel = OrderHistoryViewModel(env.orders)

        viewModel.load()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.orders.isEmpty())
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun historialConErrorDeRedMuestraMensaje() = runTest {
        env.signIn()
        env.api.failure = ConnectException("sin red")
        val viewModel = OrderHistoryViewModel(env.orders)

        viewModel.load()
        advanceUntilIdle()

        assertEquals(ApiErrorMapper.MSG_NO_CONNECTION, viewModel.state.value.error)
    }

    @Test
    fun detalleDelPedidoMuestraSusLineasYTotal() = runTest {
        env.signIn()
        env.cart.add(3, 2)
        val created = env.orders.createOrder().getOrThrow()
        val viewModel = OrderDetailViewModel(created.order.id, env.orders)

        advanceUntilIdle()
        viewModel.load()
        advanceUntilIdle()

        val detail = viewModel.state.value.detail
        assertEquals(137000.0, detail?.order?.total ?: 0.0, 0.001)
        assertEquals("Paleta de Sombras Nude", detail?.lines?.first()?.productName)
    }

    @Test
    fun detalleDePedidoInexistenteMuestraError() = runTest {
        env.signIn()
        val viewModel = OrderDetailViewModel(55, env.orders)

        advanceUntilIdle()

        assertEquals("El pedido 55 no existe.", viewModel.state.value.error)
    }
}
