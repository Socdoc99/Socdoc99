package com.calculadora.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * CONCEPTO: Paleta de colores
 * ─────────────────────────────────────────────────────────────────────────────
 * Definimos los colores de la calculadora inspirados en el diseño de iPhone:
 *
 *  Fondo         → Negro puro
 *  Botones función (AC, +/-, %)  → Gris claro  (#A5A5A5)
 *  Botones número (0-9, .)       → Gris oscuro  (#333333)
 *  Botones operador (+, −, ×, ÷, =) → Naranja  (#FF9F0A)
 *
 * Usamos valores hexadecimales ARGB: 0xFF = totalmente opaco.
 * En Compose, Color() acepta un Long en formato 0xAARRGGBB.
 */

// Fondo de la pantalla
val CalcBackground = Color(0xFF000000)

// Botones de función (AC, +/-, %)
val CalcFunctionButton = Color(0xFFA5A5A5)

// Botones de número (0–9, punto decimal)
val CalcNumberButton = Color(0xFF333333)

// Botones de operación (+, −, ×, ÷, =)
val CalcOperatorButton = Color(0xFFFF9F0A)
