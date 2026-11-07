package com.beautyshop.movil.util

import com.beautyshop.movil.domain.model.Product
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.Normalizer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Formatos de presentación para dinero y fechas. */
object Formatters {

    private val copSymbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    /** Formatea un valor en pesos colombianos, por ejemplo `$ 25.900`. */
    fun cop(value: Double): String = "$ " + DecimalFormat("#,##0", copSymbols).format(value)

    /** Formatea una fecha como `27/07/2026 17:05`; devuelve "Sin fecha" si es nula. */
    fun date(value: LocalDateTime?): String = value?.format(dateFormat) ?: "Sin fecha"

    /** Pone en mayúscula la primera letra del estado del pedido ("pendiente" -> "Pendiente"). */
    fun status(value: String): String = value.replaceFirstChar { it.uppercase() }
}

/** Filtros del catálogo. */
object ProductSearch {

    const val ALL_CATEGORIES = "Todas"

    /** Quita tildes y pasa a minúsculas para comparar textos sin importar acentos. */
    fun normalize(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .lowercase()
            .trim()
}

/** Búsqueda de productos por nombre o categoría y filtro por categoría. */
object ProductFilter {

    /** Categorías disponibles: "Todas" seguida de las categorías distintas, ordenadas. */
    fun categories(products: List<Product>): List<String> =
        listOf(ProductSearch.ALL_CATEGORIES) + products.map { it.category }.distinct().sorted()

    /**
     * Filtra [products] cuyo nombre o categoría contiene [query] (sin distinguir mayúsculas ni tildes)
     * y que pertenezcan a [category] (o a cualquiera si es "Todas").
     */
    fun apply(products: List<Product>, query: String, category: String): List<Product> {
        val text = ProductSearch.normalize(query)
        return products.filter { product ->
            val matchesCategory = category == ProductSearch.ALL_CATEGORIES || product.category == category
            val matchesText = text.isEmpty() ||
                ProductSearch.normalize(product.name).contains(text) ||
                ProductSearch.normalize(product.category).contains(text)
            matchesCategory && matchesText
        }
    }
}

/** Construcción de la URL de las fotos de producto. */
object ImageUrls {

    /**
     * Devuelve la URL de la foto: [image] tal cual si ya es http(s), `baseUrl + "images/" + image` si es un
     * nombre de archivo (con o sin "/" final en [baseUrl]), o null si [image] está vacía.
     */
    fun productImage(baseUrl: String, image: String): String? {
        val name = image.trim()
        return when {
            name.isEmpty() -> null
            name.startsWith("http://", ignoreCase = true) || name.startsWith("https://", ignoreCase = true) -> name
            else -> baseUrl.trimEnd('/') + "/images/" + name.trimStart('/')
        }
    }
}
