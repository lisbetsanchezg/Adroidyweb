package com.beautyshop.movil.domain.model

import java.time.LocalDateTime

/** Usuario autenticado en la tienda. */
data class User(
    val id: Long,
    val name: String,
    val email: String
)

/** Producto del catálogo. [image] es el nombre del archivo que guarda el backend (no es una URL). */
data class Product(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val stock: Int,
    val image: String
)

/** Línea del carrito con el nombre del producto ya resuelto. */
data class CartLine(
    val productId: Long,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int
) {
    /** Valor de la línea: precio unitario por cantidad. */
    val subtotal: Double get() = unitPrice * quantity
}

/** Carrito de compras del usuario. */
data class Cart(val lines: List<CartLine> = emptyList()) {
    /** Total del carrito en pesos colombianos. */
    val total: Double get() = lines.sumOf { it.subtotal }

    /** Cantidad total de unidades (para la insignia de la barra inferior). */
    val itemCount: Int get() = lines.sumOf { it.quantity }

    val isEmpty: Boolean get() = lines.isEmpty()
}

/** Cabecera de un pedido. */
data class Order(
    val id: Long,
    val date: LocalDateTime?,
    val status: String,
    val total: Double
)

/** Línea de un pedido. */
data class OrderLine(
    val productId: Long,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int
) {
    val subtotal: Double get() = unitPrice * quantity
}

/** Pedido con sus líneas. */
data class OrderDetail(
    val order: Order,
    val lines: List<OrderLine>
)
