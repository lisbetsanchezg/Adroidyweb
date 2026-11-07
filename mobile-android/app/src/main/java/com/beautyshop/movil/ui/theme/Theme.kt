package com.beautyshop.movil.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

/* Paleta tomada de las variables CSS de la tienda web (frontend/src/index.css). */
val RosaOscuro = Color(0xFFD81072)
val Rosa = Color(0xFFFF4DA6)
val RosaHot = Color(0xFFFF1A8C)
val RosaSuave = Color(0xFFFFE3F2)
val RosaFondo = Color(0xFFFFEEF8)
val Texto = Color(0xFF3B2F4C)
val TextoSuave = Color(0xFF7F6582)
val Borde = Color(0xFFF0D8E7)
val ErrorColor = Color(0xFFB3261E)

private val colors = lightColorScheme(
    primary = RosaOscuro,
    onPrimary = Color.White,
    primaryContainer = RosaSuave,
    onPrimaryContainer = Color(0xFF5C0A33),
    secondary = Rosa,
    onSecondary = Color.White,
    secondaryContainer = RosaSuave,
    onSecondaryContainer = Texto,
    background = RosaFondo,
    onBackground = Texto,
    surface = Color.White,
    onSurface = Texto,
    surfaceVariant = RosaSuave,
    onSurfaceVariant = TextoSuave,
    outline = Borde,
    outlineVariant = Borde,
    error = ErrorColor,
    onError = Color.White
)

private val typography = Typography(
    headlineMedium = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
)

private val shapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

/** Tema Material 3 rosado de BeautyShop. */
@Composable
fun BeautyShopTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, typography = typography, shapes = shapes, content = content)
}
