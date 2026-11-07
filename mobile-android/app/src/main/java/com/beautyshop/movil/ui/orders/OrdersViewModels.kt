package com.beautyshop.movil.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beautyshop.movil.data.repository.OrderRepository
import com.beautyshop.movil.domain.model.Order
import com.beautyshop.movil.domain.model.OrderDetail
import com.beautyshop.movil.util.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado del historial de pedidos. */
data class OrderHistoryUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val orders: List<Order> = emptyList()
)

/** ViewModel del historial de pedidos del usuario. */
class OrderHistoryViewModel(private val repository: OrderRepository) : ViewModel() {

    private val _state = MutableStateFlow(OrderHistoryUiState())
    val state: StateFlow<OrderHistoryUiState> = _state.asStateFlow()

    fun load() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            _state.value = repository.getOrders().fold(
                onSuccess = { OrderHistoryUiState(isLoading = false, orders = it) },
                onFailure = { OrderHistoryUiState(isLoading = false, error = it.userMessage()) }
            )
        }
    }
}

/** Estado del detalle de un pedido. */
data class OrderDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val detail: OrderDetail? = null
)

/** ViewModel del detalle de un pedido. */
class OrderDetailViewModel(
    private val orderId: Long,
    private val repository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OrderDetailUiState())
    val state: StateFlow<OrderDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = OrderDetailUiState(isLoading = true)
        viewModelScope.launch {
            _state.value = repository.getOrderDetail(orderId).fold(
                onSuccess = { OrderDetailUiState(isLoading = false, detail = it) },
                onFailure = { OrderDetailUiState(isLoading = false, error = it.userMessage()) }
            )
        }
    }
}
