package com.calculadora.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calculadora.CalculatorAction
import com.calculadora.CalculatorOperation
import com.calculadora.CalculatorViewModel
import com.calculadora.ui.theme.CalcBackground

/**
 * CalculatorScreen — Pantalla principal de la calculadora
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * CONCEPTO: viewModel()
 * La función viewModel() de Compose (lifecycle-viewmodel-compose) obtiene
 * o crea el ViewModel asociado al ViewModelStoreOwner más cercano
 * (normalmente la Activity). La misma instancia se reutiliza mientras
 * la Activity vive.
 *
 * CONCEPTO: collectAsState()
 * Convierte un StateFlow en un State<T> de Compose.
 * Cada vez que el StateFlow emite un nuevo valor, el composable
 * que lo lee se RECOMPONE automáticamente (se redibuja).
 *
 * CONCEPTO: by (delegado de propiedades)
 * `val state by viewModel.state.collectAsState()` desenvuelve el State<T>
 * automáticamente. Sin 'by' habría que escribir state.value cada vez.
 *
 * CONCEPTO: BoxWithConstraints
 * A diferencia de Box, BoxWithConstraints expone maxWidth y maxHeight
 * del contenedor dentro de su lambda. Permite crear layouts adaptativos
 * calculando tamaños en función del espacio disponible.
 * Aquí lo usamos para calcular el tamaño exacto de los botones.
 *
 * CONCEPTO: Layout del teclado estilo iPhone
 * ┌──────────────────────────────────────┐
 * │          [DISPLAY]                   │  ← número actual (alineado a la derecha)
 * ├──────────────────────────────────────┤
 * │  [ AC ]  [+/-]  [ % ]  [ ÷ ]        │  ← funciones (gris) + operador (naranja)
 * │  [ 7  ]  [ 8 ]  [ 9 ]  [ × ]        │
 * │  [ 4  ]  [ 5 ]  [ 6 ]  [ − ]        │
 * │  [ 1  ]  [ 2 ]  [ 3 ]  [ + ]        │
 * │  [  0 (doble ancho)  ]  [.]  [ = ]  │
 * └──────────────────────────────────────┘
 */
@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    calculatorViewModel: CalculatorViewModel = viewModel()
) {
    // Observar el estado del ViewModel — se recompone cuando cambia
    val state by calculatorViewModel.state.collectAsState()

    val spacing = 12.dp

    /**
     * BoxWithConstraints: mide el ancho disponible para calcular botones.
     * systemBarsPadding(): evita que el contenido quede debajo de la barra
     * de estado o de los botones de navegación del sistema.
     */
    BoxWithConstraints(
        modifier = modifier
            .background(CalcBackground)
            .systemBarsPadding()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // Calculamos el tamaño de cada botón:
        // 4 botones por fila + 3 espacios entre ellos = ancho total
        val buttonSize = (maxWidth - spacing * 3) / 4

        /**
         * CONCEPTO: Column
         * Apila sus hijos verticalmente.
         * Arrangement.spacedBy(spacing) añade espacio igual entre cada hijo.
         * Alignment.BottomCenter alinea la columna al fondo del Box padre,
         * tal como hace la calculadora del iPhone.
         */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {

            // ── DISPLAY ─────────────────────────────────────────────────────
            /**
             * CONCEPTO: Texto adaptativo
             * Ajustamos el tamaño de fuente según la longitud del número:
             *  ≤6 chars  → 80sp (tamaño máximo)
             *  7-9 chars → 60sp
             *  10+ chars → 48sp
             */
            val displayFontSize = when {
                state.displayValue.length > 9 -> 48.sp
                state.displayValue.length > 6 -> 60.sp
                else                          -> 80.sp
            }

            Text(
                text = state.displayValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                textAlign = TextAlign.End,
                fontSize = displayFontSize,
                fontWeight = FontWeight.Light,
                color = Color.White,
                maxLines = 1
            )

            // ── FILA 1: AC/C · +/- · % · ÷ ─────────────────────────────────
            /**
             * CONCEPTO: Row
             * Distribuye sus hijos horizontalmente.
             * Arrangement.spacedBy(spacing) añade espacio entre ellos.
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                // El botón cambia de "AC" a "C" cuando hay algo en pantalla
                val clearLabel = if (state.displayValue != "0") "C" else "AC"

                CalculatorButton(
                    text = clearLabel,
                    buttonType = ButtonType.FUNCTION,
                    modifier = Modifier.size(buttonSize),
                    buttonSize = buttonSize,
                    onClick = { calculatorViewModel.onAction(CalculatorAction.Clear) }
                )
                CalculatorButton(
                    text = "+/-",
                    buttonType = ButtonType.FUNCTION,
                    modifier = Modifier.size(buttonSize),
                    buttonSize = buttonSize,
                    onClick = { calculatorViewModel.onAction(CalculatorAction.ToggleSign) }
                )
                CalculatorButton(
                    text = "%",
                    buttonType = ButtonType.FUNCTION,
                    modifier = Modifier.size(buttonSize),
                    buttonSize = buttonSize,
                    onClick = { calculatorViewModel.onAction(CalculatorAction.Percent) }
                )
                CalculatorButton(
                    text = "÷",
                    buttonType = ButtonType.OPERATOR,
                    modifier = Modifier.size(buttonSize),
                    buttonSize = buttonSize,
                    isActive = state.pendingOperation == CalculatorOperation.DIVIDE && state.isNewEntry,
                    onClick = { calculatorViewModel.onAction(CalculatorAction.Operation(CalculatorOperation.DIVIDE)) }
                )
            }

            // ── FILA 2: 7 · 8 · 9 · × ──────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                listOf(7, 8, 9).forEach { n ->
                    CalculatorButton(
                        text = n.toString(),
                        buttonType = ButtonType.NUMBER,
                        modifier = Modifier.size(buttonSize),
                        buttonSize = buttonSize,
                        onClick = { calculatorViewModel.onAction(CalculatorAction.Number(n)) }
                    )
                }
                CalculatorButton(
                    text = "×",
                    buttonType = ButtonType.OPERATOR,
                    modifier = Modifier.size(buttonSize),
                    buttonSize = buttonSize,
                    isActive = state.pendingOperation == CalculatorOperation.MULTIPLY && state.isNewEntry,
                    onClick = { calculatorViewModel.onAction(CalculatorAction.Operation(CalculatorOperation.MULTIPLY)) }
                )
            }

            // ── FILA 3: 4 · 5 · 6 · − ──────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                listOf(4, 5, 6).forEach { n ->
                    CalculatorButton(
                        text = n.toString(),
                        buttonType = ButtonType.NUMBER,
                        modifier = Modifier.size(buttonSize),
                        buttonSize = buttonSize,
                        onClick = { calculatorViewModel.onAction(CalculatorAction.Number(n)) }
                    )
                }
                CalculatorButton(
                    text = "−",
                    buttonType = ButtonType.OPERATOR,
                    modifier = Modifier.size(buttonSize),
                    buttonSize = buttonSize,
                    isActive = state.pendingOperation == CalculatorOperation.SUBTRACT && state.isNewEntry,
                    onClick = { calculatorViewModel.onAction(CalculatorAction.Operation(CalculatorOperation.SUBTRACT)) }
                )
            }

            // ── FILA 4: 1 · 2 · 3 · + ──────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                listOf(1, 2, 3).forEach { n ->
                    CalculatorButton(
                        text = n.toString(),
                        buttonType = ButtonType.NUMBER,
                        modifier = Modifier.size(buttonSize),
                        buttonSize = buttonSize,
                        onClick = { calculatorViewModel.onAction(CalculatorAction.Number(n)) }
                    )
                }
                CalculatorButton(
                    text = "+",
                    buttonType = ButtonType.OPERATOR,
                    modifier = Modifier.size(buttonSize),
                    buttonSize = buttonSize,
                    isActive = state.pendingOperation == CalculatorOperation.ADD && state.isNewEntry,
                    onClick = { calculatorViewModel.onAction(CalculatorAction.Operation(CalculatorOperation.ADD)) }
                )
            }

            // ── FILA 5: 0 (doble ancho) · . · = ────────────────────────────
            /**
             * CONCEPTO: Tamaño del botón "0"
             * El "0" ocupa exactamente: 2 × buttonSize + 1 × spacing
             * Esto es equivalente al ancho de dos botones con su gap entre ellos,
             * replicando fielmente el diseño de la calculadora del iPhone.
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                CalculatorButton(
                    text = "0",
                    buttonType = ButtonType.NUMBER,
                    modifier = Modifier
                        .width(buttonSize * 2 + spacing)
                        .height(buttonSize),
                    buttonSize = buttonSize,
                    isWide = true,
                    onClick = { calculatorViewModel.onAction(CalculatorAction.Number(0)) }
                )
                CalculatorButton(
                    text = ".",
                    buttonType = ButtonType.NUMBER,
                    modifier = Modifier.size(buttonSize),
                    buttonSize = buttonSize,
                    onClick = { calculatorViewModel.onAction(CalculatorAction.Decimal) }
                )
                CalculatorButton(
                    text = "=",
                    buttonType = ButtonType.OPERATOR,
                    modifier = Modifier.size(buttonSize),
                    buttonSize = buttonSize,
                    onClick = { calculatorViewModel.onAction(CalculatorAction.Calculate) }
                )
            }
        }
    }
}
