package com.beautyshop.movil.data.repository

import com.beautyshop.movil.data.remote.BeautyShopApi
import com.beautyshop.movil.data.remote.dto.UserRequest
import com.beautyshop.movil.domain.model.User
import com.beautyshop.movil.util.AppError
import com.beautyshop.movil.util.safeApiCall
import kotlinx.coroutines.flow.StateFlow

/**
 * Módulo de autenticación: registro, inicio y cierre de sesión.
 * [onSignOut] permite limpiar datos de otros módulos (por ejemplo el carrito en memoria).
 */
class AuthRepository(
    private val api: BeautyShopApi,
    private val session: SessionManager,
    private val onSignOut: () -> Unit = {}
) {
    val currentUser: StateFlow<User?> = session.user

    /** Inicia sesión; si los datos son correctos guarda al usuario en la sesión. */
    suspend fun login(email: String, password: String): Result<User> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(AppError("Ingresa tu correo y tu contraseña."))
        }
        return safeApiCall { api.login(UserRequest(email = email.trim(), password = password)).toDomain() }
            .onSuccess { session.signIn(it) }
    }

    /** Registra un usuario nuevo y deja la sesión iniciada con él. */
    suspend fun register(name: String, email: String, password: String): Result<User> {
        val validationError = validateRegistration(name, email, password)
        if (validationError != null) return Result.failure(AppError(validationError))
        return safeApiCall {
            api.register(UserRequest(name = name.trim(), email = email.trim(), password = password)).toDomain()
        }.onSuccess { session.signIn(it) }
    }

    fun logout() {
        session.signOut()
        onSignOut()
    }

    companion object {
        const val MIN_PASSWORD_LENGTH = 6

        /** Devuelve el mensaje del primer dato inválido, o null si todo es correcto. */
        fun validateRegistration(name: String, email: String, password: String): String? = when {
            name.isBlank() -> "Escribe tu nombre."
            !EMAIL_REGEX.matches(email.trim()) -> "Escribe un correo válido, por ejemplo ana@correo.com."
            password.length < MIN_PASSWORD_LENGTH ->
                "La contraseña debe tener al menos $MIN_PASSWORD_LENGTH caracteres."
            else -> null
        }

        private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    }
}
