package com.calculadora.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * CONCEPTO: Material Design 3 Theme
 * ─────────────────────────────────────────────────────────────────────────────
 * Material 3 es el sistema de diseño de Google para Android moderno.
 * Un tema en Compose define:
 *  - ColorScheme: colores primarios, de fondo, superficie, etc.
 *  - Typography: estilos de texto predefinidos
 *
 * darkColorScheme(): esquema de colores oscuros (perfecto para la calculadora).
 *
 * CONCEPTO: @Composable
 * Una función marcada con @Composable puede llamar a otras funciones
 * @Composable y forma parte del árbol de UI. No devuelve un valor —
 * "emite" elementos de UI al árbol.
 */

private val CalculatorColorScheme = darkColorScheme(
    primary        = CalcOperatorButton,    // Naranja — color principal
    background     = CalcBackground,         // Negro — fondo de la app
    surface        = CalcNumberButton,       // Gris oscuro — superficie de botones
    onPrimary      = Color.White,            // Texto sobre color primario
    onBackground   = Color.White,            // Texto sobre fondo
    onSurface      = Color.White,            // Texto sobre superficie
)

/**
 * CalculadoraTheme — envuelve toda la app con el tema personalizado.
 *
 * @param content árbol de composables hijo que hereda este tema.
 *
 * CONCEPTO: Slot API / Composable lambda
 * El parámetro 'content: @Composable () -> Unit' es una lambda composable.
 * Permite anidar composables dentro del tema usando el bloque { }.
 */
@Composable
fun CalculadoraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CalculatorColorScheme,
        typography = Typography,
        content = content
    )
}
