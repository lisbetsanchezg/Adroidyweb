package com.beautyshop.movil.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beautyshop.movil.ui.components.ErrorState
import com.beautyshop.movil.ui.components.LoadingState
import com.beautyshop.movil.ui.components.ProductImage
import com.beautyshop.movil.util.Formatters

/** Detalle de un producto con selector de cantidad y botón para agregar al carrito. */
@Composable
fun ProductDetailScreen(viewModel: ProductDetailViewModel, snackbarHostState: SnackbarHostState) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.messageShown()
        }
    }

    val product = state.product
    when {
        state.isLoading -> LoadingState()
        state.error != null || product == null ->
            ErrorState(state.error ?: "No se encontró el producto.", onRetry = viewModel::load)
        else -> Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProductImage(product.image, 160.dp)
            Spacer(Modifier.height(16.dp))
            Text(product.name, style = MaterialTheme.typography.headlineMedium)
            Text(
                product.category.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                Formatters.cop(product.price),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            Text(product.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                "Disponibles: ${product.stock} unidades",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconButton(onClick = viewModel::decrease) {
                    Icon(Icons.Filled.Remove, contentDescription = "Disminuir cantidad")
                }
                Text("${state.quantity}", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = viewModel::increase) {
                    Icon(Icons.Filled.Add, contentDescription = "Aumentar cantidad")
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = viewModel::addToCart, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Agregar al carrito")
            }
        }
    }
}
