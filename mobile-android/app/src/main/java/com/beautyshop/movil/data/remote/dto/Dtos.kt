package com.beautyshop.movil.data.remote.dto

import java.math.BigDecimal

/*
 * Objetos de transferencia que reflejan el JSON exacto del backend (Spring Boot).
 * Gson crea los objetos sin pasar por el constructor, por eso todos los campos son anulables.
 */

/** Cuerpo de registro e inicio de sesión: el backend usa la misma clase `User` para ambos. */
data class UserRequest(
    val name: String? = null,
    val email: String,
    val password: String
)

data class UserDto(
    val id: Long? = null,
    val name: String? = null,
    val email: String? = null
)

data class ProductDto(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val price: BigDecimal? = null,
    val category: String? = null,
    val stock: Int? = null,
    val image: String? = null
)

/** Cuerpo para agregar o cambiar la cantidad de un producto en el carrito. */
data class CartItemRequest(
    val idProducto: Long? = null,
    val cantidad: Int
)

data class CartItemDto(
    val id: Long? = null,
    val idUsuario: Long? = null,
    val idProducto: Long? = null,
    val cantidad: Int? = null,
    val precioUnitario: BigDecimal? = null
)

data class CartResponseDto(
    val idUsuario: Long? = null,
    val items: List<CartItemDto>? = null,
    val total: BigDecimal? = null
)

data class OrderDto(
    val idPedido: Long? = null,
    val idUsuario: Long? = null,
    val fecha: String? = null,
    val estado: String? = null,
    val total: BigDecimal? = null
)

data class OrderItemDto(
    val id: Long? = null,
    val idPedido: Long? = null,
    val idProducto: Long? = null,
    val cantidad: Int? = null,
    val precioUnitario: BigDecimal? = null
)

data class OrderDetailDto(
    val order: OrderDto? = null,
    val items: List<OrderItemDto>? = null
)
