package com.beautyshop.movil.util

import com.beautyshop.movil.domain.model.Cart
import com.beautyshop.movil.domain.model.CartLine
import com.beautyshop.movil.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class FormattersAndFilterTest {

    private val catalog = listOf(
        Product(1, "Labial Mate Rojo Pasión", "", 25900.0, "labios", 40, ""),
        Product(2, "Base Líquida Natural", "", 45000.0, "rostro", 25, ""),
        Product(3, "Paleta de Sombras Nude", "", 68500.0, "ojos", 15, ""),
        Product(4, "Máscara de Pestañas", "", 32000.0, "ojos", 30, "")
    )

    @Test
    fun copUsaPuntoComoSeparadorDeMiles() {
        assertEquals("$ 25.900", Formatters.cop(25900.0))
        assertEquals("$ 1.250.000", Formatters.cop(1250000.0))
        assertEquals("$ 0", Formatters.cop(0.0))
    }

    @Test
    fun fechaSeFormateaEnDiaMesAnio() {
        assertEquals("27/07/2026 17:05", Formatters.date(LocalDateTime.of(2026, 7, 27, 17, 5)))
        assertEquals("Sin fecha", Formatters.date(null))
    }

    @Test
    fun estadoEmpiezaEnMayuscula() {
        assertEquals("Pendiente", Formatters.status("pendiente"))
    }

    @Test
    fun busquedaPorNombreIgnoraMayusculasYTildes() {
        assertEquals(listOf(1L), ProductFilter.apply(catalog, "LABIAL", ProductSearch.ALL_CATEGORIES).map { it.id })
        assertEquals(listOf(4L), ProductFilter.apply(catalog, "mascara", ProductSearch.ALL_CATEGORIES).map { it.id })
        assertEquals(listOf(2L), ProductFilter.apply(catalog, "liquida", ProductSearch.ALL_CATEGORIES).map { it.id })
    }

    @Test
    fun busquedaPorCategoriaEncuentraProductosDeEsaCategoria() {
        assertEquals(listOf(3L, 4L), ProductFilter.apply(catalog, "ojos", ProductSearch.ALL_CATEGORIES).map { it.id })
    }

    @Test
    fun filtroDeCategoriaSeCombinaConLaBusqueda() {
        assertEquals(listOf(3L), ProductFilter.apply(catalog, "paleta", "ojos").map { it.id })
        assertEquals(emptyList<Product>(), ProductFilter.apply(catalog, "labial", "ojos"))
    }

    @Test
    fun textoVacioDevuelveTodosLosProductos() {
        assertEquals(4, ProductFilter.apply(catalog, "   ", ProductSearch.ALL_CATEGORIES).size)
    }

    @Test
    fun categoriasIncluyenTodasYNoSeRepiten() {
        assertEquals(listOf("Todas", "labios", "ojos", "rostro"), ProductFilter.categories(catalog))
    }

    @Test
    fun totalDelCarritoSumaSubtotalesDeLasLineas() {
        val cart = Cart(
            listOf(
                CartLine(1, "Labial", 25900.0, 2),
                CartLine(2, "Base", 45000.0, 1)
            )
        )
        assertEquals(96800.0, cart.total, 0.001)
        assertEquals(3, cart.itemCount)
        assertEquals(0.0, Cart().total, 0.001)
    }
}
