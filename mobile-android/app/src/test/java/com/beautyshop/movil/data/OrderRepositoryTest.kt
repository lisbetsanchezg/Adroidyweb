package com.beautyshop.movil.data

import com.beautyshop.movil.fakes.TestEnvironment
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class OrderRepositoryTest {

    private val env = TestEnvironment()

    @Test
    fun crearPedidoDevuelveDetalleConNombresYVaciaElCarrito() = runTest {
        env.signIn()
        env.cart.add(1, 2)
        env.cart.add(3, 1)

        val detail = env.orders.createOrder().getOrThrow()

        assertEquals("pendiente", detail.order.status)
        assertEquals(2 * 25900.0 + 68500.0, detail.order.total, 0.001)
        assertEquals(listOf("Labial Mate Rojo Pasión", "Paleta de Sombras Nude"), detail.lines.map { it.productName })
        assertEquals(listOf(2, 1), detail.lines.map { it.quantity })
        assertTrue(env.cart.cart.value.isEmpty)
        assertEquals(0.0, env.cart.cart.value.total, 0.001)
    }

    @Test
    fun crearPedidoConCarritoVacioMuestraMensajeYNoLlamaAlServidor() = runTest {
        env.signIn()

        val result = env.orders.createOrder()

        assertEquals(
            "Tu carrito está vacío. Agrega productos antes de hacer el pedido.",
            result.exceptionOrNull()?.message
        )
        assertTrue(env.api.calls.none { it == "createOrder" })
    }

    @Test
    fun siElServidorFallaAlCrearElPedidoElCarritoSeConserva() = runTest {
        env.signIn()
        env.cart.add(1)
        env.api.failure = java.net.ConnectException("sin red")

        val result = env.orders.createOrder()

        assertTrue(result.isFailure)
        assertEquals(1, env.cart.cart.value.itemCount)
    }

    @Test
    fun historialListaLosPedidosDelMasRecienteAlMasAntiguo() = runTest {
        env.signIn()
        env.cart.add(1)
        env.orders.createOrder()
        env.cart.add(2)
        env.orders.createOrder()

        val orders = env.orders.getOrders().getOrThrow()

        assertEquals(listOf(2L, 1L), orders.map { it.id })
        assertEquals(LocalDateTime.of(2026, 7, 27, 17, 2), orders.first().date)
    }

    @Test
    fun historialVacioDevuelveListaVacia() = runTest {
        env.signIn()

        assertTrue(env.orders.getOrders().getOrThrow().isEmpty())
    }

    @Test
    fun historialSinSesionPideIniciarSesion() = runTest {
        assertEquals("Inicia sesión para ver tus pedidos.", env.orders.getOrders().exceptionOrNull()?.message)
    }

    @Test
    fun detalleDeUnPedidoInexistenteMuestraElMensajeDelServidor() = runTest {
        env.signIn()

        assertEquals("El pedido 77 no existe.", env.orders.getOrderDetail(77).exceptionOrNull()?.message)
    }

    @Test
    fun detalleDeUnPedidoTraeSusLineas() = runTest {
        env.signIn()
        env.cart.add(2, 3)
        val created = env.orders.createOrder().getOrThrow()

        val detail = env.orders.getOrderDetail(created.order.id).getOrThrow()

        assertEquals(1, detail.lines.size)
        assertEquals("Base Líquida Natural", detail.lines.first().productName)
        assertEquals(135000.0, detail.lines.first().subtotal, 0.001)
    }
}
