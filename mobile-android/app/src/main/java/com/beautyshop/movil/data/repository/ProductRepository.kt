package com.beautyshop.movil.data.repository

import com.beautyshop.movil.data.remote.BeautyShopApi
import com.beautyshop.movil.domain.model.Product
import com.beautyshop.movil.util.safeApiCall

/** Módulo de catálogo. Guarda en memoria el último listado para resolver nombres y detalles. */
class ProductRepository(private val api: BeautyShopApi) {

    @Volatile
    private var cache: List<Product> = emptyList()

    /** Descarga el catálogo completo. */
    suspend fun getProducts(): Result<List<Product>> =
        safeApiCall { api.getProducts().map { it.toDomain() } }.onSuccess { cache = it }

    /** Producto del último listado descargado, o null si no está. */
    fun findCached(productId: Long): Product? = cache.firstOrNull { it.id == productId }

    /** Mapa id -> nombre del último listado, usado para mostrar carrito y pedidos. */
    fun cachedNames(): Map<Long, String> = cache.associate { it.id to it.name }

    /** Descarga el catálogo si todavía no se ha cargado. */
    suspend fun ensureLoaded() {
        if (cache.isEmpty()) getProducts()
    }
}
