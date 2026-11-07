package com.beautyshop.movil.data.repository

import com.beautyshop.movil.data.remote.BeautyShopApi
import com.beautyshop.movil.data.remote.dto.CartItemRequest
import com.beautyshop.movil.data.remote.dto.CartResponseDto
import com.beautyshop.movil.domain.model.Cart
import com.beautyshop.movil.util.AppError
import com.beautyshop.movil.util.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Módulo de carrito. Expone el carrito como [StateFlow] para que las pantallas
 * (catálogo, carrito) se actualicen solas cuando cambia.
 */
class CartRepository(
    private val api: BeautyShopApi,
    private val session: SessionManager,
    private val products: ProductRepository
) {
    private val _cart = MutableStateFlow(Cart())

    /** Carrito actual del usuario. */
    val cart: StateFlow<Cart> = _cart.asStateFlow()

    /** Vuelve a pedir el carrito al servidor. */
    suspend fun refresh(): Result<Cart> = call { userId -> api.getCart(userId) }

    /** Agrega [quantity] unidades de [productId]; el servidor suma si ya estaba en el carrito. */
    suspend fun add(productId: Long, quantity: Int = 1): Result<Cart> {
        products.ensureLoaded()
        val inCart = _cart.value.lines.firstOrNull { it.productId == productId }?.quantity ?: 0
        quantityError(productId, inCart + quantity)?.let { return Result.failure(AppError(it)) }
        return call { userId -> api.addItem(userId, CartItemRequest(productId, quantity)) }
    }

    /** Fija la cantidad de un producto del carrito; debe estar entre 1 y el máximo permitido. */
    suspend fun setQuantity(productId: Long, quantity: Int): Result<Cart> {
        products.ensureLoaded()
        quantityError(productId, quantity)?.let { return Result.failure(AppError(it)) }
        return call { userId -> api.updateQuantity(userId, productId, CartItemRequest(cantidad = quantity)) }
    }

    /** Quita un producto del carrito. */
    suspend fun remove(productId: Long): Result<Cart> = call { userId -> api.removeItem(userId, productId) }

    /** Vacía el estado local (después de crear un pedido o de cerrar sesión). */
    fun clearLocal() {
        _cart.value = Cart()
    }

    /** Cantidad máxima permitida para un producto: su existencia, sin pasar de [MAX_QUANTITY]. */
    fun maxQuantityFor(productId: Long): Int {
        val stock = products.findCached(productId)?.stock ?: MAX_QUANTITY
        return minOf(stock, MAX_QUANTITY)
    }

    private fun quantityError(productId: Long, quantity: Int): String? {
        val max = maxQuantityFor(productId)
        return when {
            quantity < 1 -> "La cantidad mínima es 1."
            quantity > max -> "Solo puedes pedir hasta $max unidades de este producto."
            else -> null
        }
    }

    private suspend fun call(request: suspend (Long) -> CartResponseDto): Result<Cart> {
        val userId = session.user.value?.id
            ?: return Result.failure(AppError("Inicia sesión para usar el carrito."))
        products.ensureLoaded()
        return safeApiCall { request(userId).toDomain(products.cachedNames()) }
            .onSuccess { _cart.value = it }
    }

    companion object {
        const val MAX_QUANTITY = 99
    }
}
