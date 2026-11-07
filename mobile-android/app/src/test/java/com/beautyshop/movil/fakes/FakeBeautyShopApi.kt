package com.beautyshop.movil.fakes

import com.beautyshop.movil.data.remote.BeautyShopApi
import com.beautyshop.movil.data.remote.dto.CartItemDto
import com.beautyshop.movil.data.remote.dto.CartItemRequest
import com.beautyshop.movil.data.remote.dto.CartResponseDto
import com.beautyshop.movil.data.remote.dto.OrderDetailDto
import com.beautyshop.movil.data.remote.dto.OrderDto
import com.beautyshop.movil.data.remote.dto.OrderItemDto
import com.beautyshop.movil.data.remote.dto.ProductDto
import com.beautyshop.movil.data.remote.dto.UserDto
import com.beautyshop.movil.data.remote.dto.UserRequest
import com.beautyshop.movil.data.repository.SessionStorage
import com.beautyshop.movil.domain.model.User
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.math.BigDecimal

/** Crea una `HttpException` con el mismo cuerpo JSON que devuelve el backend: `{"error": "..."}`. */
fun httpError(code: Int, message: String?): HttpException {
    val json = if (message == null) "" else "{\"error\":\"$message\"}"
    val body = json.toResponseBody("application/json".toMediaType())
    return HttpException(Response.error<Any>(code, body))
}

/** Almacenamiento de sesión en memoria para las pruebas. */
class InMemorySessionStorage(var stored: User? = null) : SessionStorage {
    override fun read(): User? = stored
    override fun write(user: User?) {
        stored = user
    }
}

/**
 * API falsa que imita las reglas del backend real (usuarios, carrito y pedidos en memoria).
 * Si [failure] tiene valor, todas las llamadas lanzan esa excepción (por ejemplo, sin red).
 */
class FakeBeautyShopApi : BeautyShopApi {

    var failure: Throwable? = null
    var productsFailure: Throwable? = null
    val calls = mutableListOf<String>()

    val products = mutableListOf(
        product(1, "Labial Mate Rojo Pasión", 25900, "labios", 40),
        product(2, "Base Líquida Natural", 45000, "rostro", 25),
        product(3, "Paleta de Sombras Nude", 68500, "ojos", 15),
        product(4, "Máscara de Pestañas Volumen", 32000, "ojos", 3)
    )

    private val users = mutableListOf<Pair<UserDto, String>>()
    private val carts = mutableMapOf<Long, LinkedHashMap<Long, Int>>()
    private val orders = mutableListOf<Pair<OrderDto, List<OrderItemDto>>>()
    private var nextUserId = 1L
    private var nextOrderId = 1L

    private fun enter(name: String) {
        calls += name
        failure?.let { throw it }
    }

    override suspend fun getProducts(): List<ProductDto> {
        enter("getProducts")
        productsFailure?.let { throw it }
        return products.toList()
    }

    override suspend fun register(user: UserRequest): UserDto {
        enter("register")
        if (users.any { it.first.email == user.email }) throw httpError(400, "El correo ya esta registrado.")
        val created = UserDto(nextUserId++, user.name, user.email)
        users += created to user.password
        return created
    }

    override suspend fun login(credentials: UserRequest): UserDto {
        enter("login")
        val found = users.firstOrNull { it.first.email == credentials.email && it.second == credentials.password }
        return found?.first ?: throw httpError(401, "Correo o contrasena incorrectos.")
    }

    override suspend fun getCart(userId: Long): CartResponseDto {
        enter("getCart")
        return cartOf(userId)
    }

    override suspend fun addItem(userId: Long, item: CartItemRequest): CartResponseDto {
        enter("addItem")
        val productId = item.idProducto ?: throw httpError(400, "Producto obligatorio.")
        if (item.cantidad <= 0) throw httpError(400, "La cantidad debe ser mayor que 0.")
        if (products.none { it.id == productId }) throw httpError(404, "El producto $productId no existe.")
        val cart = carts.getOrPut(userId) { LinkedHashMap() }
        cart[productId] = (cart[productId] ?: 0) + item.cantidad
        return cartOf(userId)
    }

    override suspend fun updateQuantity(userId: Long, productId: Long, item: CartItemRequest): CartResponseDto {
        enter("updateQuantity")
        if (item.cantidad <= 0) throw httpError(400, "La cantidad debe ser mayor que 0.")
        val cart = carts[userId]
        if (cart == null || productId !in cart) throw httpError(404, "El producto no esta en el carrito.")
        cart[productId] = item.cantidad
        return cartOf(userId)
    }

    override suspend fun removeItem(userId: Long, productId: Long): CartResponseDto {
        enter("removeItem")
        val cart = carts[userId]
        if (cart == null || cart.remove(productId) == null) throw httpError(404, "El producto no esta en el carrito.")
        return cartOf(userId)
    }

    override suspend fun createOrder(userId: Long): OrderDetailDto {
        enter("createOrder")
        val cart = cartOf(userId)
        if (cart.items.orEmpty().isEmpty()) {
            throw httpError(400, "El carrito esta vacio; no se puede crear el pedido.")
        }
        val orderId = nextOrderId++
        val header = OrderDto(orderId, userId, "2026-07-27T17:0${orderId}:00", "pendiente", cart.total)
        val lines = cart.items.orEmpty().map {
            OrderItemDto(orderId * 10, orderId, it.idProducto, it.cantidad, it.precioUnitario)
        }
        orders += header to lines
        carts[userId]?.clear()
        return OrderDetailDto(header, lines)
    }

    override suspend fun getOrders(userId: Long): List<OrderDto> {
        enter("getOrders")
        return orders.map { it.first }.filter { it.idUsuario == userId }
    }

    override suspend fun getOrderDetail(orderId: Long): OrderDetailDto {
        enter("getOrderDetail")
        val found = orders.firstOrNull { it.first.idPedido == orderId }
            ?: throw httpError(404, "El pedido $orderId no existe.")
        return OrderDetailDto(found.first, found.second)
    }

    private fun cartOf(userId: Long): CartResponseDto {
        val items = carts[userId].orEmpty().map { (productId, quantity) ->
            val price = products.first { it.id == productId }.price
            CartItemDto(null, userId, productId, quantity, price)
        }
        val total = items.fold(BigDecimal.ZERO) { sum, item ->
            sum + item.precioUnitario!! * BigDecimal(item.cantidad!!)
        }
        return CartResponseDto(userId, items, total)
    }

    private companion object {
        fun product(id: Long, name: String, price: Long, category: String, stock: Int) =
            ProductDto(id, name, "Descripción de $name", BigDecimal(price), category, stock, "imagen-$id.jpg")
    }
}
