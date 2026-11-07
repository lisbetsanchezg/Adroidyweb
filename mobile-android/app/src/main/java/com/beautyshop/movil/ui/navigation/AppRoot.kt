package com.beautyshop.movil.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.beautyshop.movil.ui.AppViewModelProvider
import com.beautyshop.movil.ui.auth.AuthScreen
import com.beautyshop.movil.ui.auth.AuthViewModel
import com.beautyshop.movil.ui.cart.CartScreen
import com.beautyshop.movil.ui.cart.CartViewModel
import com.beautyshop.movil.ui.catalog.CatalogScreen
import com.beautyshop.movil.ui.catalog.ProductDetailScreen
import com.beautyshop.movil.ui.orders.OrderDetailScreen
import com.beautyshop.movil.ui.orders.OrderHistoryScreen

/** Rutas de navegación de la aplicación. */
object Routes {
    const val ARG_PRODUCT_ID = "productId"
    const val ARG_ORDER_ID = "orderId"

    const val CATALOG = "catalogo"
    const val CART = "carrito"
    const val ORDERS = "pedidos"
    const val PRODUCT = "producto/{$ARG_PRODUCT_ID}"
    const val ORDER = "pedido/{$ARG_ORDER_ID}"

    fun product(id: Long) = "producto/$id"
    fun order(id: Long) = "pedido/$id"
}

private data class BottomItem(val route: String, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomItem(Routes.CATALOG, "Catálogo", Icons.Filled.Store),
    BottomItem(Routes.CART, "Carrito", Icons.Filled.ShoppingCart),
    BottomItem(Routes.ORDERS, "Pedidos", Icons.Filled.ReceiptLong)
)

/** Raíz de la interfaz: muestra el ingreso si no hay sesión, o la tienda si ya hay un usuario. */
@Composable
fun AppRoot() {
    val authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val user by authViewModel.user.collectAsState()

    if (user == null) {
        androidx.compose.material3.Surface(color = MaterialTheme.colorScheme.background) {
            AuthScreen(authViewModel)
        }
    } else {
        MainScaffold(userName = user?.name.orEmpty(), onLogout = authViewModel::logout)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold(userName: String, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val cartViewModel: CartViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val cart by cartViewModel.cart.collectAsState()
    val backStack by navController.currentBackStackEntryAsState()
    val destination = backStack?.destination
    val isTopLevel = bottomItems.any { item -> destination?.hierarchy?.any { it.route == item.route } == true }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isTopLevel) "BeautyShop · $userName" else "BeautyShop") },
                navigationIcon = {
                    if (!isTopLevel) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                bottomItems.forEach { item ->
                    val selected = destination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = { navigateTopLevel(navController, item.route) },
                        label = { Text(item.label) },
                        icon = {
                            if (item.route == Routes.CART && cart.itemCount > 0) {
                                BadgedBox(badge = { Badge { Text("${cart.itemCount}") } }) {
                                    Icon(item.icon, contentDescription = null)
                                }
                            } else {
                                Icon(item.icon, contentDescription = null)
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = Routes.CATALOG, modifier = Modifier.padding(padding)) {
            composable(Routes.CATALOG) {
                CatalogScreen(
                    viewModel(factory = AppViewModelProvider.Factory),
                    snackbarHostState,
                    onProductClick = { navController.navigate(Routes.product(it)) }
                )
            }
            composable(
                Routes.PRODUCT,
                arguments = listOf(navArgument(Routes.ARG_PRODUCT_ID) { type = NavType.LongType })
            ) {
                ProductDetailScreen(viewModel(factory = AppViewModelProvider.Factory), snackbarHostState)
            }
            composable(Routes.CART) {
                CartScreen(
                    cartViewModel,
                    snackbarHostState,
                    onOrderCreated = { navController.navigate(Routes.order(it)) }
                )
            }
            composable(Routes.ORDERS) {
                OrderHistoryScreen(
                    viewModel(factory = AppViewModelProvider.Factory),
                    onOrderClick = { navController.navigate(Routes.order(it)) }
                )
            }
            composable(
                Routes.ORDER,
                arguments = listOf(navArgument(Routes.ARG_ORDER_ID) { type = NavType.LongType })
            ) {
                OrderDetailScreen(viewModel(factory = AppViewModelProvider.Factory))
            }
        }
    }
}

private fun navigateTopLevel(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
