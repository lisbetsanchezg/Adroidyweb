package com.beautyshop.movil.data.repository

import com.beautyshop.movil.data.remote.dto.CartResponseDto
import com.beautyshop.movil.data.remote.dto.OrderDetailDto
import com.beautyshop.movil.data.remote.dto.OrderDto
import com.beautyshop.movil.data.remote.dto.ProductDto
import com.beautyshop.movil.data.remote.dto.UserDto
import com.beautyshop.movil.domain.model.Cart
import com.beautyshop.movil.domain.model.CartLine
import com.beautyshop.movil.domain.model.Order
import com.beautyshop.movil.domain.model.OrderDetail
import com.beautyshop.movil.domain.model.OrderLine
import com.beautyshop.movil.domain.model.Product
import com.beautyshop.movil.domain.model.User
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

/* Conversión de DTO (JSON del backend) a modelos del dominio. */

fun UserDto.toDomain(): User = User(id = id ?: 0L, name = name.orEmpty(), email = email.orEmpty())

fun ProductDto.toDomain(): Product = Product(
    id = id ?: 0L,
    name = name.orEmpty(),
    description = description.orEmpty(),
    price = price?.toDouble() ?: 0.0,
    category = category.orEmpty(),
    stock = stock ?: 0,
    image = image.orEmpty()
)

/** El carrito del backend solo trae ids; los nombres se toman del catálogo ([names]). */
fun CartResponseDto.toDomain(names: Map<Long, String>): Cart = Cart(
    items.orEmpty().map { item ->
        val productId = item.idProducto ?: 0L
        CartLine(
            productId = productId,
            productName = names[productId] ?: "Producto #$productId",
            unitPrice = item.precioUnitario?.toDouble() ?: 0.0,
            quantity = item.cantidad ?: 0
        )
    }
)

fun OrderDto.toDomain(): Order = Order(
    id = idPedido ?: 0L,
    date = parseDate(fecha),
    status = estado.orEmpty(),
    total = total?.toDouble() ?: 0.0
)

fun OrderDetailDto.toDomain(names: Map<Long, String>): OrderDetail = OrderDetail(
    order = (order ?: OrderDto()).toDomain(),
    lines = items.orEmpty().map { item ->
        val productId = item.idProducto ?: 0L
        OrderLine(
            productId = productId,
            productName = names[productId] ?: "Producto #$productId",
            unitPrice = item.precioUnitario?.toDouble() ?: 0.0,
            quantity = item.cantidad ?: 0
        )
    }
)

private fun parseDate(text: String?): LocalDateTime? = try {
    text?.let { LocalDateTime.parse(it) }
} catch (ignored: DateTimeParseException) {
    null
}
