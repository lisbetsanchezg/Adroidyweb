package com.beautyshop.movil.fakes

import com.beautyshop.movil.data.repository.AuthRepository
import com.beautyshop.movil.data.repository.CartRepository
import com.beautyshop.movil.data.repository.OrderRepository
import com.beautyshop.movil.data.repository.ProductRepository
import com.beautyshop.movil.data.repository.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/** Reemplaza `Dispatchers.Main` por un dispatcher de pruebas, para poder probar los ViewModels en la JVM. */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(val dispatcher: TestDispatcher = StandardTestDispatcher()) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}

/** Arma los repositorios reales sobre una [FakeBeautyShopApi], igual que hace `AppContainer`. */
class TestEnvironment(val api: FakeBeautyShopApi = FakeBeautyShopApi()) {
    val storage = InMemorySessionStorage()
    val session = SessionManager(storage)
    val products = ProductRepository(api)
    val cart = CartRepository(api, session, products)
    val orders = OrderRepository(api, session, products, cart)
    val auth = AuthRepository(api, session, onSignOut = cart::clearLocal)

    /** Registra un usuario y deja la sesión iniciada. */
    suspend fun signIn(email: String = "ana@correo.com") {
        auth.register("Ana", email, "secreto1").getOrThrow()
    }
}
