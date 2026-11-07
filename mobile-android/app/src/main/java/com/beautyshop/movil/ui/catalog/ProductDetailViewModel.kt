package com.beautyshop.movil.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beautyshop.movil.data.repository.CartRepository
import com.beautyshop.movil.data.repository.ProductRepository
import com.beautyshop.movil.domain.model.Product
import com.beautyshop.movil.util.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado de la pantalla de detalle de producto. */
data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val product: Product? = null,
    val quantity: Int = 1,
    val message: String? = null
)

/** ViewModel del detalle: muestra un producto y permite elegir la cantidad antes de agregarlo. */
class ProductDetailViewModel(
    private val productId: Long,
    private val products: ProductRepository,
    private val cart: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailUiState())
    val state: StateFlow<ProductDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val cached = products.findCached(productId)
            if (cached != null) {
                _state.value = _state.value.copy(isLoading = false, product = cached)
                return@launch
            }
            products.getProducts().fold(
                onSuccess = { list ->
                    val found = list.firstOrNull { it.id == productId }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        product = found,
                        error = if (found == null) "No se encontró el producto." else null
                    )
                },
                onFailure = { _state.value = _state.value.copy(isLoading = false, error = it.userMessage()) }
            )
        }
    }

    fun increase() {
        val max = cart.maxQuantityFor(productId)
        _state.value = _state.value.copy(quantity = (_state.value.quantity + 1).coerceAtMost(max))
    }

    fun decrease() {
        _state.value = _state.value.copy(quantity = (_state.value.quantity - 1).coerceAtLeast(1))
    }

    fun addToCart() {
        val product = _state.value.product ?: return
        viewModelScope.launch {
            val result = cart.add(product.id, _state.value.quantity)
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
