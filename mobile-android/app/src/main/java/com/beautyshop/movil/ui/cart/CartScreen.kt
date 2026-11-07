package com.beautyshop.movil.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beautyshop.movil.domain.model.CartLine
import com.beautyshop.movil.ui.components.EmptyState
import com.beautyshop.movil.ui.components.ErrorState
import com.beautyshop.movil.ui.components.LoadingState
import com.beautyshop.movil.util.Formatters

/** Carrito: líneas con cantidad editable, total en COP y botón para crear el pedido. */
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    snackbarHostState: SnackbarHostState,
    onOrderCreated: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val cart by viewModel.cart.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.messageShown()
        }
    }
    LaunchedEffect(state.createdOrderId) {
        state.createdOrderId?.let {
            viewModel.orderOpened()
            onOrderCreated(it)
        }
    }

    when {
        state.isLoading && cart.isEmpty -> LoadingState()
        state.error != null -> ErrorState(state.error.orEmpty(), onRetry = viewModel::load)
        cart.isEmpty -> EmptyState(
            Icons.Filled.ShoppingCart,
            "Tu carrito está vacío",
            "Agrega productos desde el catálogo para hacer tu pedido."
        )
        else -> Column(Modifier.fillMaxWidth()) {
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cart.lines, key = { it.productId }) { line ->
                    CartLineCard(
                        line,
                        onIncrease = { viewModel.increase(line) },
                        onDecrease = { viewModel.decrease(line) },
                        onRemove = { viewModel.remove(line) }
                    )
                }
            }
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", style = MaterialTheme.typography.titleLarge)
                        Text(
                            Formatters.cop(cart.total),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = viewModel::checkout,
                        enabled = !state.isPlacingOrder,
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        if (state.isPlacingOrder) {
                            CircularProgressIndicator(
                                Modifier.size(22.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Crear pedido")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartLineCard(line: CartLine, onIncrease: () -> Unit, onDecrease: () -> Unit, onRemove: () -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(line.productName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Quitar ${line.productName}",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Text(
                "${Formatters.cop(line.unitPrice)} c/u",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease) {
                        Icon(Icons.Filled.Remove, contentDescription = "Disminuir cantidad")
                    }
                    Text("${line.quantity}", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = onIncrease) {
                        Icon(Icons.Filled.Add, contentDescription = "Aumentar cantidad")
                    }
                }
                Text(
                    Formatters.cop(line.subtotal),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
