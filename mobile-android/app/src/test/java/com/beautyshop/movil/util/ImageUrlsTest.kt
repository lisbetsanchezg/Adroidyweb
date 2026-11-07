package com.beautyshop.movil.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ImageUrlsTest {

    private val base = "http://10.0.2.2:8090/"

    @Test
    fun imagenVaciaNoTieneUrl() {
        assertNull(ImageUrls.productImage(base, ""))
        assertNull(ImageUrls.productImage(base, "   "))
    }

    @Test
    fun nombreDeArchivoSeAgregaALaUrlBase() {
        assertEquals("http://10.0.2.2:8090/images/labial-rojo.jpg", ImageUrls.productImage(base, "labial-rojo.jpg"))
    }

    @Test
    fun urlBaseSinBarraFinalTambienFunciona() {
        assertEquals(
            "http://10.0.2.2:8090/images/labial-rojo.jpg",
            ImageUrls.productImage("http://10.0.2.2:8090", "labial-rojo.jpg")
        )
    }

    @Test
    fun urlAbsolutaSeUsaTalCual() {
        assertEquals("https://cdn.ejemplo.com/a.jpg", ImageUrls.productImage(base, "https://cdn.ejemplo.com/a.jpg"))
        assertEquals("http://cdn.ejemplo.com/a.jpg", ImageUrls.productImage(base, "http://cdn.ejemplo.com/a.jpg"))
    }

    @Test
    fun barraInicialDelNombreNoDuplicaSeparador() {
        assertEquals("http://10.0.2.2:8090/images/a.jpg", ImageUrls.productImage(base, "/a.jpg"))
    }
}
