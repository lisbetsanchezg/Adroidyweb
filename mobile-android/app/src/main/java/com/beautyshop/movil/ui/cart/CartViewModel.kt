package com.beautyshop.movil.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beautyshop.movil.data.repository.CartRepository
import com.beautyshop.movil.data.repository.OrderRepository
import com.beautyshop.movil.domain.model.Cart
import com.beautyshop.movil.domain.model.CartLine
import com.beautyshop.movil.util.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado de la pantalla de carrito. El contenido del carrito se observa aparte en [CartViewModel.cart]. */
data class CartUiState(
    val isLoading: Boolean = true,
    val isPlacingOrder: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    /** Número del pedido recién creado; la pantalla navega a su detalle y luego llama a `orderOpened`. */
    val createdOrderId: Long? = null
)

/** ViewModel del módulo de carrito: cambiar cantidades, quitar productos y crear el pedido. */
class CartViewModel(
    private val cartRepository: CartRepository,
    private val orders: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CartUiState())
    val state: StateFlow<CartUiState> = _state.asStateFlow()

    /** Carrito actual, compartido con el resto de la aplicación. */
    val cart: StateFlow<Cart> = cartRepository.cart

    fun load() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = cartRepository.refresh()
            _state.value = _state.value.copy(isLoading = false, error = result.exceptionOrNull()?.userMessage())
        }
    }

    fun increase(line: CartLine) = changeQuantity(line.productId, line.quantity + 1)

    fun decrease(line: CartLine) = changeQuantity(line.productId, line.quantity - 1)

    fun remove(line: CartLine) {
        viewModelScope.launch {
            val result = cartRepository.remove(line.productId)
            _state.value = _state.value.copy(message = result.exceptionOrNull()?.userMessage())
        }
    }

    /** Crea el pedido con el contenido del carrito. */
    fun checkout() {
        if (_state.value.isPlacingOrder) return
        _state.value = _state.value.copy(isPlacingOrder = true, message = null)
        viewModelScope.launch {
            val result = orders.createOrder()
            _state.value = result.fold(
                onSuccess = {
                    _state.value.copy(
                        isPlacingOrder = false,
                        createdOrderId = it.order.id,
                        message = "Pedido #${it.order.id} creado correctamente."
                    )
                },
                onFailure = { _state.value.copy(isPlacingOrder = false, message = it.userMessage()) }
            )
        }
    }

    fun orderOpened() {
        _state.value = _state.value.copy(createdOrderId = null)
    }

    fun messageShown() {
        _state.value = _state.value.copy(message = null)
    }

    private fun changeQuantity(productId: Long, quantity: Int) {
        viewModelScope.launch {
            val result = cartRepository.setQuantity(productId, quantity)
            _state.value = _state.value.copy(message = result.exceptionOrNull()?.userMessage())
        }
    }
}
