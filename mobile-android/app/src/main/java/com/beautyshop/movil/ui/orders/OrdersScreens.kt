package com.beautyshop.movil.ui.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beautyshop.movil.domain.model.Order
import com.beautyshop.movil.ui.components.EmptyState
import com.beautyshop.movil.ui.components.ErrorState
import com.beautyshop.movil.ui.components.LoadingState
import com.beautyshop.movil.util.Formatters

/** Historial de pedidos del usuario; se recarga cada vez que se abre la pantalla. */
@Composable
fun OrderHistoryScreen(viewModel: OrderHistoryViewModel, onOrderClick: (Long) -> Unit) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.load() }

    when {
        state.isLoading && state.orders.isEmpty() -> LoadingState()
        state.error != null -> ErrorState(state.error.orEmpty(), onRetry = viewModel::load)
        state.orders.isEmpty() -> EmptyState(
            Icons.Filled.ReceiptLong,
            "Aún no tienes pedidos",
            "Cuando hagas tu primer pedido aparecerá aquí."
        )
        else -> LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.orders, key = { it.id }) { order -> OrderCard(order, onClick = { onOrderClick(order.id) }) }
        }
    }
}

@Composable
private fun OrderCard(order: Order, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Pedido #${order.id}", style = MaterialTheme.typography.titleMedium)
                Text(
                    Formatters.status(order.status),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                Formatters.date(order.date),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                Formatters.cop(order.total),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/** Detalle de un pedido: estado, fecha, líneas y total. */
@Composable
fun OrderDetailScreen(viewModel: OrderDetailViewModel) {
    val state by viewModel.state.collectAsState()
    val detail = state.detail

    when {
        state.isLoading -> LoadingState()
        state.error != null || detail == null ->
            ErrorState(state.error ?: "No se encontró el pedido.", onRetry = viewModel::load)
        else -> LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Pedido #${detail.order.id}", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "${Formatters.status(detail.order.status)} · ${Formatters.date(detail.order.date)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
            }
            items(detail.lines) { line ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(line.productName, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${line.quantity} × ${Formatters.cop(line.unitPrice)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(Formatters.cop(line.subtotal), style = MaterialTheme.typography.titleMedium)
                }
            }
            item {
                HorizontalDivider()
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", style = MaterialTheme.typography.titleLarge)
                    Text(
                        Formatters.cop(detail.order.total),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
