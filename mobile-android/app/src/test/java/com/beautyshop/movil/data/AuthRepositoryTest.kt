package com.beautyshop.movil.data

import com.beautyshop.movil.fakes.TestEnvironment
import com.beautyshop.movil.util.ApiErrorMapper
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.ConnectException

class AuthRepositoryTest {

    private val env = TestEnvironment()

    @Test
    fun registroValidoGuardaLaSesion() = runTest {
        val result = env.auth.register("Ana", "ana@correo.com", "secreto1")

        assertEquals("Ana", result.getOrThrow().name)
        assertEquals("ana@correo.com", env.session.user.value?.email)
        assertNotNull(env.storage.stored)
    }

    @Test
    fun registroConNombreVacioNoLlamaAlServidor() = runTest {
        val result = env.auth.register("  ", "ana@correo.com", "secreto1")

        assertEquals("Escribe tu nombre.", result.exceptionOrNull()?.message)
        assertTrue(env.api.calls.isEmpty())
    }

    @Test
    fun registroConCorreoInvalidoMuestraMensaje() = runTest {
        val result = env.auth.register("Ana", "ana-correo", "secreto1")

        assertEquals("Escribe un correo válido, por ejemplo ana@correo.com.", result.exceptionOrNull()?.message)
    }

    @Test
    fun registroConContrasenaCortaMuestraMensaje() = runTest {
        val result = env.auth.register("Ana", "ana@correo.com", "123")

        assertEquals("La contraseña debe tener al menos 6 caracteres.", result.exceptionOrNull()?.message)
    }

    @Test
    fun registroConCorreoRepetidoMuestraElMensajeDelServidor() = runTest {
        env.auth.register("Ana", "ana@correo.com", "secreto1")
        env.auth.logout()

        val result = env.auth.register("Otra Ana", "ana@correo.com", "secreto2")

        assertEquals("El correo ya esta registrado.", result.exceptionOrNull()?.message)
        assertNull(env.session.user.value)
    }

    @Test
    fun loginCorrectoDevuelveElUsuarioYGuardaLaSesion() = runTest {
        env.auth.register("Ana", "ana@correo.com", "secreto1")
        env.auth.logout()

        val result = env.auth.login("  ana@correo.com ", "secreto1")

        assertEquals("Ana", result.getOrThrow().name)
        assertEquals(1L, env.session.user.value?.id)
    }

    @Test
    fun loginConContrasenaIncorrectaSeTraduceACredencialesIncorrectas() = runTest {
        env.auth.register("Ana", "ana@correo.com", "secreto1")
        env.auth.logout()

        val result = env.auth.login("ana@correo.com", "equivocada")

        assertEquals(ApiErrorMapper.MSG_INVALID_CREDENTIALS, result.exceptionOrNull()?.message)
        assertNull(env.session.user.value)
    }

    @Test
    fun loginConCamposVaciosNoLlamaAlServidor() = runTest {
        val result = env.auth.login("", "")

        assertEquals("Ingresa tu correo y tu contraseña.", result.exceptionOrNull()?.message)
        assertTrue(env.api.calls.isEmpty())
    }

    @Test
    fun loginSinConexionMuestraMensajeDeRed() = runTest {
        env.api.failure = ConnectException("Connection refused")

        val result = env.auth.login("ana@correo.com", "secreto1")

        assertEquals(ApiErrorMapper.MSG_NO_CONNECTION, result.exceptionOrNull()?.message)
    }

    @Test
    fun cerrarSesionLimpiaUsuarioYCarrito() = runTest {
        env.signIn()
        env.cart.add(1)
        assertEquals(1, env.cart.cart.value.itemCount)

        env.auth.logout()

        assertNull(env.session.user.value)
        assertNull(env.storage.stored)
        assertTrue(env.cart.cart.value.isEmpty)
    }
}
