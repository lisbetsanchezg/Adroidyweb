package com.beautyshop.movil.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beautyshop.movil.data.repository.CartRepository
import com.beautyshop.movil.data.repository.ProductRepository
import com.beautyshop.movil.domain.model.Product
import com.beautyshop.movil.util.ProductFilter
import com.beautyshop.movil.util.ProductSearch
import com.beautyshop.movil.util.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado de la pantalla de catálogo. [visible] ya viene filtrado por [query] y [category]. */
data class CatalogUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val products: List<Product> = emptyList(),
    val query: String = "",
    val category: String = ProductSearch.ALL_CATEGORIES,
    val message: String? = null
) {
    val categories: List<String> get() = ProductFilter.categories(products)
    val visible: List<Product> get() = ProductFilter.apply(products, query, category)
}

/** ViewModel del módulo de catálogo: carga, búsqueda, filtro por categoría y agregar al carrito. */
class CatalogViewModel(
    private val products: ProductRepository,
    private val cart: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogUiState())
    val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = products.getProducts()
            _state.value = result.fold(
                onSuccess = { _state.value.copy(isLoading = false, products = it) },
                onFailure = { _state.value.copy(isLoading = false, error = it.userMessage()) }
            )
            cart.refresh()
        }
    }

    fun onQueryChange(query: String) {
        _state.value = _state.value.copy(query = query)
    }

    fun onCategoryChange(category: String) {
        _state.value = _state.value.copy(category = category)
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val result = cart.add(product.id)
            _state.value = _state.value.copy(
                message = result.fold(
                    onSuccess = { "${product.name} se agregó al carrito." },
                    onFailure = { it.userMessage() }
                )
            )
        }
    }

    fun messageShown() {
        _state.value = _state.value.copy(message = null)
    }
}
