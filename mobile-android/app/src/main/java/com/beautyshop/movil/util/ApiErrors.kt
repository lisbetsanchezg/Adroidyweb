package com.beautyshop.movil.util

import com.google.gson.JsonParser
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/** Error de la aplicación cuyo [message] ya está listo para mostrarse al usuario, en español. */
class AppError(message: String) : Exception(message)

/** Convierte excepciones de red y HTTP en mensajes claros para el usuario. */
object ApiErrorMapper {

    const val MSG_INVALID_CREDENTIALS = "Correo o contraseña incorrectos."
    const val MSG_NO_CONNECTION = "No se pudo conectar con el servidor. Revisa tu conexión e inténtalo de nuevo."
    const val MSG_TIMEOUT = "El servidor tardó demasiado en responder. Inténtalo de nuevo."
    const val MSG_SERVER = "El servidor tuvo un problema. Inténtalo de nuevo más tarde."
    const val MSG_NOT_FOUND = "No se encontró lo que buscabas."
    const val MSG_BAD_REQUEST = "Los datos enviados no son válidos."
    const val MSG_UNEXPECTED = "Ocurrió un error inesperado."

    /** Devuelve el mensaje en español que corresponde a [throwable]. */
    fun toMessage(throwable: Throwable): String = when (throwable) {
        is AppError -> throwable.message ?: MSG_UNEXPECTED
        is HttpException -> fromHttp(throwable)
        is SocketTimeoutException -> MSG_TIMEOUT
        is IOException -> MSG_NO_CONNECTION
        else -> MSG_UNEXPECTED
    }

    private fun fromHttp(exception: HttpException): String = when (val code = exception.code()) {
        401 -> MSG_INVALID_CREDENTIALS
        400, 404 -> serverMessage(exception) ?: if (code == 404) MSG_NOT_FOUND else MSG_BAD_REQUEST
        else -> MSG_SERVER
    }

    /** Lee la clave "error" del JSON `{"error": "..."}` que devuelve el backend. */
    private fun serverMessage(exception: HttpException): String? = try {
        val body = exception.response()?.errorBody()?.string()
        if (body.isNullOrBlank()) {
            null
        } else {
            JsonParser.parseString(body).asJsonObject.get("error")?.asString?.takeIf { it.isNotBlank() }
        }
    } catch (ignored: Exception) {
        null
    }
}

/**
 * Ejecuta una llamada al backend y devuelve el resultado como [Result].
 * Los fallos se traducen a [AppError] con mensaje en español. La cancelación de corrutinas se propaga.
 */
suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancelled: CancellationException) {
    throw cancelled
} catch (error: Exception) {
    Result.failure(AppError(ApiErrorMapper.toMessage(error)))
}

/** Mensaje de un fallo para mostrar en pantalla. */
fun Throwable.userMessage(): String = ApiErrorMapper.toMessage(this)
