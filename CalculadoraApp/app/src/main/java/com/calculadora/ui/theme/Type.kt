package com.calculadora.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * CONCEPTO: Typography (Tipografía)
 * ─────────────────────────────────────────────────────────────────────────────
 * Material 3 define una escala tipográfica con roles predefinidos:
 * displayLarge, headlineLarge, bodyLarge, labelLarge, etc.
 *
 * La calculadora usa principalmente displayLarge para el número en pantalla
 * y bodyLarge para texto secundario.
 *
 * sp (scale-independent pixels): unidad de medida para texto que respeta
 * la preferencia de tamaño de fuente del usuario en Accesibilidad.
 */
val Typography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 80.sp,
        lineHeight = 88.sp,
        letterSpacing = (-1.5).sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
