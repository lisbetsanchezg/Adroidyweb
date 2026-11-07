package com.beautyshop.movil.util

import com.beautyshop.movil.fakes.FakeBeautyShopApi
import com.beautyshop.movil.fakes.httpError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class ApiErrorMapperTest {

    @Test
    fun error401SeMuestraComoCredencialesIncorrectas() {
        val message = ApiErrorMapper.toMessage(httpError(401, "Correo o contrasena incorrectos."))
        assertEquals(ApiErrorMapper.MSG_INVALID_CREDENTIALS, message)
    }

    @Test
    fun error400UsaElMensajeDelServidor() {
        val message = ApiErrorMapper.toMessage(httpError(400, "El correo ya esta registrado."))
        assertEquals("El correo ya esta registrado.", message)
    }

    @Test
    fun error404SinCuerpoUsaMensajeGenerico() {
        assertEquals(ApiErrorMapper.MSG_NOT_FOUND, ApiErrorMapper.toMessage(httpError(404, null)))
    }

    @Test
    fun error500NoMuestraDetallesInternosDelServidor() {
        val message = ApiErrorMapper.toMessage(httpError(500, "Communications link failure en la base de datos"))
        assertEquals(ApiErrorMapper.MSG_SERVER, message)
    }

    @Test
    fun sinRedMuestraMensajeDeConexion() {
        assertEquals(ApiErrorMapper.MSG_NO_CONNECTION, ApiErrorMapper.toMessage(ConnectException("Connection refused")))
        assertEquals(ApiErrorMapper.MSG_NO_CONNECTION, ApiErrorMapper.toMessage(IOException("sin red")))
    }

    @Test
    fun tiempoAgotadoTieneMensajePropio() {
        assertEquals(ApiErrorMapper.MSG_TIMEOUT, ApiErrorMapper.toMessage(SocketTimeoutException("timeout")))
    }

    @Test
    fun errorDesconocidoUsaMensajeInesperado() {
        assertEquals(ApiErrorMapper.MSG_UNEXPECTED, ApiErrorMapper.toMessage(IllegalStateException("raro")))
    }

    @Test
    fun safeApiCallConvierteExcepcionEnAppErrorConMensaje() = runTest {
        val result = safeApiCall { throw httpError(401, "x") }
        assertTrue(result.exceptionOrNull() is AppError)
        assertEquals(ApiErrorMapper.MSG_INVALID_CREDENTIALS, result.exceptionOrNull()?.message)
    }

    @Test(expected = CancellationException::class)
    fun safeApiCallNoTragaLaCancelacion() = runTest {
        safeApiCall<Unit> { throw CancellationException("cancelado") }
    }

    @Test
    fun safeApiCallDevuelveElValorSiNoHayError() = runTest {
        val api = FakeBeautyShopApi()
        assertEquals(4, safeApiCall { api.getProducts() }.getOrThrow().size)
    }
}
