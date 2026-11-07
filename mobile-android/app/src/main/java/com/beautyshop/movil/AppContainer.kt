package com.beautyshop.movil

import android.content.Context
import com.beautyshop.movil.data.remote.ApiClient
import com.beautyshop.movil.data.remote.BeautyShopApi
import com.beautyshop.movil.data.repository.AuthRepository
import com.beautyshop.movil.data.repository.CartRepository
import com.beautyshop.movil.data.repository.OrderRepository
import com.beautyshop.movil.data.repository.ProductRepository
import com.beautyshop.movil.data.repository.SessionManager
import com.beautyshop.movil.data.repository.SharedPreferencesSessionStorage

/**
 * Contenedor de dependencias (inyección manual por constructor).
 * Crea una sola vez la API y los repositorios, y se comparte desde [BeautyShopApp].
 */
class AppContainer(context: Context, api: BeautyShopApi? = null) {

    private val service: BeautyShopApi = api ?: ApiClient.create(BuildConfig.BASE_URL, BuildConfig.DEBUG)

    val session = SessionManager(SharedPreferencesSessionStorage(context))
    val productRepository = ProductRepository(service)
    val cartRepository = CartRepository(service, session, productRepository)
    val orderRepository = OrderRepository(service, session, productRepository, cartRepository)
    val authRepository = AuthRepository(service, session, onSignOut = cartRepository::clearLocal)
}
