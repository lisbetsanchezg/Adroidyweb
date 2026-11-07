package com.beautyshop.movil.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Construye el cliente Retrofit a partir de la URL base (configurada en BuildConfig.BASE_URL). */
object ApiClient {

    private const val TIMEOUT_SECONDS = 15L

    /**
     * @param baseUrl URL base del backend, terminada en "/".
     * @param logRequests si es true, registra método, URL y código de respuesta en Logcat.
     *        Nunca se registra el cuerpo, porque login y registro envían la contraseña.
     */
    fun create(baseUrl: String, logRequests: Boolean): BeautyShopApi {
        val logging = HttpLoggingInterceptor().apply {
            level = if (logRequests) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        val client = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BeautyShopApi::class.java)
    }
}
