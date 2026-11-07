package com.beautyshop.movil.data.repository

import com.beautyshop.movil.data.remote.BeautyShopApi
import com.beautyshop.movil.domain.model.Order
import com.beautyshop.movil.domain.model.OrderDetail
import com.beautyshop.movil.util.AppError
import com.beautyshop.movil.util.safeApiCall

/** Módulo de pedidos: crear a partir del carrito, ver el historial y consultar el detalle. */
class OrderRepository(
    private val api: BeautyShopApi,
    private val session: SessionManager,
    private val products: ProductRepository,
    private val cart: CartRepository
) {
    /** Crea el pedido con el carrito actual. El backend vacía el carrito, por eso también se limpia el local. */
    suspend fun createOrder(): Result<OrderDetail> {
        val userId = requireUser() ?: return notSignedIn()
        if (cart.cart.value.isEmpty) {
            return Result.failure(AppError("Tu carrito está vacío. Agrega productos antes de hacer el pedido."))
        }
        products.ensureLoaded()
        return safeApiCall { api.createOrder(userId).toDomain(products.cachedNames()) }
            .onSuccess { cart.clearLocal() }
    }

    /** Historial de pedidos del usuario, del más reciente al más antiguo. */
    suspend fun getOrders(): Result<List<Order>> {
        val userId = requireUser() ?: return notSignedIn()
        return safeApiCall {
            api.getOrders(userId).map { it.toDomain() }.sortedByDescending { it.date }
        }
    }

    /** Cabecera y líneas de un pedido. */
    suspend fun getOrderDetail(orderId: Long): Result<OrderDetail> {
        products.ensureLoaded()
        return safeApiCall { api.getOrderDetail(orderId).toDomain(products.cachedNames()) }
    }

    private fun requireUser(): Long? = session.user.value?.id

    private fun <T> notSignedIn(): Result<T> =
        Result.failure(AppError("Inicia sesión para ver tus pedidos."))
}
