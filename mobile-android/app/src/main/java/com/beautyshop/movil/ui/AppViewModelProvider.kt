package com.beautyshop.movil.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.createSavedStateHandle
import com.beautyshop.movil.AppContainer
import com.beautyshop.movil.BeautyShopApp
import com.beautyshop.movil.ui.auth.AuthViewModel
import com.beautyshop.movil.ui.cart.CartViewModel
import com.beautyshop.movil.ui.catalog.CatalogViewModel
import com.beautyshop.movil.ui.catalog.ProductDetailViewModel
import com.beautyshop.movil.ui.navigation.Routes
import com.beautyshop.movil.ui.orders.OrderDetailViewModel
import com.beautyshop.movil.ui.orders.OrderHistoryViewModel

/** Fábrica de ViewModels: entrega a cada uno los repositorios del [AppContainer]. */
object AppViewModelProvider {

    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { AuthViewModel(container().authRepository) }
        initializer { CatalogViewModel(container().productRepository, container().cartRepository) }
        initializer {
            val handle = createSavedStateHandle()
            ProductDetailViewModel(
                handle.id(Routes.ARG_PRODUCT_ID),
                container().productRepository,
                container().cartRepository
            )
        }
        initializer { CartViewModel(container().cartRepository, container().orderRepository) }
        initializer { OrderHistoryViewModel(container().orderRepository) }
        initializer {
            val handle = createSavedStateHandle()
            OrderDetailViewModel(handle.id(Routes.ARG_ORDER_ID), container().orderRepository)
        }
    }

    private fun CreationExtras.container(): AppContainer =
        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BeautyShopApp).container

    private fun SavedStateHandle.id(key: String): Long = checkNotNull(get<Long>(key)) { "Falta el argumento $key" }
}
