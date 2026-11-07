package com.beautyshop.movil.data.repository

import android.content.Context
import com.beautyshop.movil.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Almacenamiento persistente del usuario con sesión iniciada. */
interface SessionStorage {
    fun read(): User?
    fun write(user: User?)
}

/** Guarda el usuario en SharedPreferences privadas de la aplicación. */
class SharedPreferencesSessionStorage(context: Context) : SessionStorage {

    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    override fun read(): User? {
        if (!prefs.contains(KEY_ID)) return null
        return User(
            id = prefs.getLong(KEY_ID, 0L),
            name = prefs.getString(KEY_NAME, "").orEmpty(),
            email = prefs.getString(KEY_EMAIL, "").orEmpty()
        )
    }

    override fun write(user: User?) {
        prefs.edit().apply {
            if (user == null) {
                clear()
            } else {
                putLong(KEY_ID, user.id)
                putString(KEY_NAME, user.name)
                putString(KEY_EMAIL, user.email)
            }
        }.apply()
    }

    private companion object {
        const val KEY_ID = "id"
        const val KEY_NAME = "nombre"
        const val KEY_EMAIL = "correo"
    }
}

/** Mantiene el usuario actual como [StateFlow] observable (patrón Observer) y lo persiste. */
class SessionManager(private val storage: SessionStorage) {

    private val _user = MutableStateFlow(storage.read())

    /** Usuario con sesión iniciada, o null si nadie ha ingresado. */
    val user: StateFlow<User?> = _user.asStateFlow()

    fun signIn(user: User) {
        storage.write(user)
        _user.value = user
    }

    fun signOut() {
        storage.write(null)
        _user.value = null
    }
}
