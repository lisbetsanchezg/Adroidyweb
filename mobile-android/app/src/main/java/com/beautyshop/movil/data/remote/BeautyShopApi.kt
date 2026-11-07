package com.beautyshop.movil.data.remote

import com.beautyshop.movil.data.remote.dto.CartItemRequest
import com.beautyshop.movil.data.remote.dto.CartResponseDto
import com.beautyshop.movil.data.remote.dto.OrderDetailDto
import com.beautyshop.movil.data.remote.dto.OrderDto
import com.beautyshop.movil.data.remote.dto.ProductDto
import com.beautyshop.movil.data.remote.dto.UserDto
import com.beautyshop.movil.data.remote.dto.UserRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Contrato HTTP con el backend de BeautyShop (puerto 8090).
 * Los errores HTTP se lanzan como `retrofit2.HttpException`.
 */
interface BeautyShopApi {

    @GET("api/products")
    suspend fun getProducts(): List<ProductDto>

    @POST("api/auth/register")
    suspend fun register(@Body user: UserRequest): UserDto

    @POST("api/auth/login")
    suspend fun login(@Body credentials: UserRequest): UserDto

    @GET("api/cart/{userId}")
    suspend fun getCart(@Path("userId") userId: Long): CartResponseDto

    @POST("api/cart/{userId}/items")
    suspend fun addItem(@Path("userId") userId: Long, @Body item: CartItemRequest): CartResponseDto

    @PUT("api/cart/{userId}/items/{productId}")
    suspend fun updateQuantity(
        @Path("userId") userId: Long,
        @Path("productId") productId: Long,
        @Body item: CartItemRequest
    ): CartResponseDto

    @DELETE("api/cart/{userId}/items/{productId}")
    suspend fun removeItem(@Path("userId") userId: Long, @Path("productId") productId: Long): CartResponseDto

    @POST("api/orders/{userId}")
    suspend fun createOrder(@Path("userId") userId: Long): OrderDetailDto

    @GET("api/orders/{userId}")
    suspend fun getOrders(@Path("userId") userId: Long): List<OrderDto>

    @GET("api/orders/detail/{orderId}")
    suspend fun getOrderDetail(@Path("orderId") orderId: Long): OrderDetailDto
}
