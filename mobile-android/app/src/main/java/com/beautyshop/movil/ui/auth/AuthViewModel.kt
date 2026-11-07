package com.beautyshop.movil.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beautyshop.movil.data.repository.AuthRepository
import com.beautyshop.movil.domain.model.User
import com.beautyshop.movil.util.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado de la pantalla de autenticación. */
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

/** ViewModel del módulo de autenticación (login, registro y cierre de sesión). */
class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    /** Usuario con sesión iniciada; la navegación cambia sola cuando deja de ser null. */
    val user: StateFlow<User?> = repository.currentUser

    fun login(email: String, password: String) = submit { repository.login(email, password) }

    fun register(name: String, email: String, password: String) =
        submit { repository.register(name, email, password) }

    fun logout() {
        repository.logout()
        _state.value = AuthUiState()
    }

    /** Borra el mensaje de error, por ejemplo al cambiar entre "Ingresar" y "Crear cuenta". */
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun submit(action: suspend () -> Result<User>) {
        if (_state.value.isLoading) return
        _state.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            val result = action()
            _state.value = AuthUiState(error = result.exceptionOrNull()?.userMessage())
        }
    }
}
