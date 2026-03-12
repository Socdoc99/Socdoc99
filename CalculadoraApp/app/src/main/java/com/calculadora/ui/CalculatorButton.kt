package com.calculadora.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calculadora.ui.theme.CalcFunctionButton
import com.calculadora.ui.theme.CalcNumberButton
import com.calculadora.ui.theme.CalcOperatorButton

/**
 * CONCEPTO: Enum para clasificar visualmente los botones
 * ─────────────────────────────────────────────────────────────────────────────
 * Cada tipo tiene colores de fondo y texto distintos:
 *  NUMBER   → gris oscuro, texto blanco
 *  FUNCTION → gris claro,  texto negro
 *  OPERATOR → naranja,     texto blanco (o invertido si está activo)
 */
enum class ButtonType {
    NUMBER,
    FUNCTION,
    OPERATOR
}

/**
 * CalculatorButton — Composable reutilizable para los botones de la calculadora
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * CONCEPTO: Composable con Modifier
 * El parámetro `modifier: Modifier = Modifier` es una práctica estándar en
 * Compose. Permite al componente padre controlar posición, tamaño, padding, etc.
 * del botón sin conocer sus detalles internos.
 *
 * CONCEPTO: Parámetros con valores por defecto
 * Kotlin permite valores por defecto en parámetros, evitando sobrecargas.
 * isWide e isActive son opcionales (por defecto false).
 *
 * CONCEPTO: ButtonDefaults.buttonColors()
 * Personaliza colores del Button de Material 3:
 *  - containerColor: color de fondo del botón
 *  - contentColor:   color por defecto del contenido (texto, iconos)
 *
 * @param text        Etiqueta del botón (dígito u operador)
 * @param buttonType  Tipo visual (NUMBER, FUNCTION, OPERATOR)
 * @param modifier    Modificador externo (tamaño, posición)
 * @param buttonSize  Altura del botón usada para calcular el tamaño de fuente
 * @param isWide      Si ocupa el doble de ancho (botón "0")
 * @param isActive    Si el operador está pendiente (invierte colores)
 * @param onClick     Callback al presionar el botón
 */
@Composable
fun CalculatorButton(
    text: String,
    buttonType: ButtonType,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 80.dp,
    isWide: Boolean = false,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    // Determinar colores según el tipo y el estado "activo"
    val backgroundColor = when {
        isActive && buttonType == ButtonType.OPERATOR -> Color.White
        buttonType == ButtonType.OPERATOR             -> CalcOperatorButton
        buttonType == ButtonType.FUNCTION             -> CalcFunctionButton
        else                                          -> CalcNumberButton
    }
    val textColor = when {
        isActive && buttonType == ButtonType.OPERATOR -> CalcOperatorButton
        buttonType == ButtonType.FUNCTION             -> Color.Black
        else                                          -> Color.White
    }

    // Tamaño de fuente proporcional al tamaño del botón
    val fontSize = (buttonSize.value * 0.42f).sp

    /**
     * CONCEPTO: CircleShape vs RoundedCornerShape
     * CircleShape → radio 50% → botón perfectamente circular
     * RoundedCornerShape(50) → radio del 50% → idéntico a CircleShape
     *   pero funciona mejor cuando el botón no es exactamente cuadrado.
     *
     * El botón "0" es rectangular (2:1) → usamos RoundedCornerShape(50)
     * para que las esquinas sean completamente redondeadas (píldora).
     */
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxSize(),
        shape = if (isWide) RoundedCornerShape(50) else CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Box(
            // El texto del botón "0" se alinea a la izquierda (igual que iPhone)
            contentAlignment = if (isWide) Alignment.CenterStart else Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = if (isWide) (buttonSize * 0.35f) else 0.dp)
        ) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.Normal,
                color = textColor
            )
        }
    }
}
