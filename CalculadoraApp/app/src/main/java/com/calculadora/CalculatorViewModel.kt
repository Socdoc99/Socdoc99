package com.calculadora

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs
import kotlin.math.floor

/**
 * CONCEPTO: ViewModel
 * ─────────────────────────────────────────────────────────────────────────────
 * El ViewModel es el componente central del patrón MVVM (Model-View-ViewModel).
 * Su responsabilidad es:
 *  1. Mantener el estado de la UI de forma independiente al ciclo de vida.
 *  2. Exponer ese estado a la UI de forma observable.
 *  3. Ejecutar la lógica de negocio cuando el usuario realiza acciones.
 *
 * ¿Por qué sobrevive rotaciones de pantalla?
 * Android destruye y recrea la Activity al rotar el dispositivo.
 * El ViewModel es gestionado por el ViewModelStore y solo se destruye
 * cuando el usuario abandona definitivamente la pantalla.
 *
 * CONCEPTO: StateFlow
 * ─────────────────────────────────────────────────────────────────────────────
 * StateFlow es un flujo de datos observable que:
 *  - Siempre tiene un valor actual (a diferencia de Flow normal)
 *  - Notifica a todos sus colectores cuando el valor cambia
 *  - Es thread-safe
 *
 * Patrón de exposición segura:
 *  - _state: MutableStateFlow (privado, solo el ViewModel puede modificarlo)
 *  - state:  StateFlow (público, la UI solo puede leerlo)
 *
 * CONCEPTO: Patrón UDF — Unidirectional Data Flow
 * ─────────────────────────────────────────────────────────────────────────────
 *   UI  ──(onAction)──►  ViewModel  ──(state)──►  UI
 *   ↑___________________________|
 *
 * Los datos fluyen en UNA sola dirección. La UI envía eventos (acciones)
 * al ViewModel, que actualiza el estado; la UI se repinta automáticamente.
 */
class CalculatorViewModel : ViewModel() {

    // Estado mutable interno (solo el ViewModel puede modificarlo)
    private val _state = MutableStateFlow(CalculatorState())

    // Estado público de solo lectura para la UI
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    /**
     * Punto de entrada único para todas las acciones del usuario.
     * El 'when' sobre una sealed class cubre exhaustivamente todos los casos.
     */
    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number   -> enterNumber(action.number)
            is CalculatorAction.Operation -> enterOperation(action.operation)
            CalculatorAction.Calculate   -> calculate()
            CalculatorAction.Clear       -> clear()
            CalculatorAction.Decimal     -> enterDecimal()
            CalculatorAction.ToggleSign  -> toggleSign()
            CalculatorAction.Percent     -> percent()
        }
    }

    // ── Acciones privadas ────────────────────────────────────────────────────

    private fun enterNumber(number: Int) {
        /**
         * CONCEPTO: StateFlow.update { }
         * 'update' recibe el estado actual y devuelve el nuevo estado.
         * Garantiza actualizaciones atómicas (thread-safe).
         * Usamos copy() de data class para modificar solo los campos necesarios.
         */
        _state.update { current ->
            // Si es un número nuevo (después de operador o de "="), reemplaza display
            if (current.isNewEntry || current.justCalculated) {
                current.copy(
                    displayValue = number.toString(),
                    isNewEntry = false,
                    justCalculated = false
                )
            } else {
                // Limitar a 9 dígitos significativos
                val digits = current.displayValue.replace("-", "").replace(".", "")
                if (digits.length >= 9) return@update current

                val newDisplay = if (current.displayValue == "0") {
                    number.toString()   // No mostrar "05", sino "5"
                } else {
                    current.displayValue + number.toString()
                }
                current.copy(displayValue = newDisplay)
            }
        }
    }

    private fun enterDecimal() {
        _state.update { current ->
            when {
                // Empezar número decimal nuevo
                current.isNewEntry || current.justCalculated -> current.copy(
                    displayValue = "0.",
                    isNewEntry = false,
                    justCalculated = false
                )
                // Solo se puede tener UN punto decimal
                !current.displayValue.contains(".") -> current.copy(
                    displayValue = current.displayValue + "."
                )
                else -> current   // Ya tiene punto — ignorar
            }
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        val current = _state.value
        val currentValue = current.displayValue.toDoubleOrNull() ?: return

        // Encadenamiento de operaciones: 5 + 3 × ... calcula el 5+3 primero
        val newFirstOperand = if (current.firstOperand != null && !current.isNewEntry) {
            performCalculation(current.firstOperand, currentValue, current.pendingOperation!!)
        } else {
            currentValue
        }

        _state.value = current.copy(
            displayValue = formatNumber(newFirstOperand),
            firstOperand = newFirstOperand,
            pendingOperation = operation,
            isNewEntry = true,
            justCalculated = false
        )
    }

    private fun calculate() {
        val current = _state.value
        val first = current.firstOperand ?: return
        val operation = current.pendingOperation ?: return
        val second = current.displayValue.toDoubleOrNull() ?: return

        val result = performCalculation(first, second, operation)

        _state.value = current.copy(
            displayValue = formatNumber(result),
            firstOperand = null,
            pendingOperation = null,
            isNewEntry = true,
            justCalculated = true
        )
    }

    private fun performCalculation(
        first: Double,
        second: Double,
        operation: CalculatorOperation
    ): Double = when (operation) {
        CalculatorOperation.ADD      -> first + second
        CalculatorOperation.SUBTRACT -> first - second
        CalculatorOperation.MULTIPLY -> first * second
        CalculatorOperation.DIVIDE   -> if (second != 0.0) first / second else Double.NaN
    }

    private fun clear() {
        // Vuelve completamente al estado inicial
        _state.value = CalculatorState()
    }

    private fun toggleSign() {
        val value = _state.value.displayValue.toDoubleOrNull() ?: return
        _state.update { it.copy(displayValue = formatNumber(-value)) }
    }

    private fun percent() {
        val value = _state.value.displayValue.toDoubleOrNull() ?: return
        _state.update { it.copy(displayValue = formatNumber(value / 100.0)) }
    }

    /**
     * Formatea un Double para mostrarlo en pantalla:
     *  - Números enteros sin decimales: 5.0 → "5"
     *  - Decimales: hasta 8 cifras, eliminando ceros finales: 3.14159 → "3.14159"
     *  - Números inválidos (NaN, Infinito): "Error"
     *
     * Marcado como 'internal' para poder testearla directamente.
     */
    internal fun formatNumber(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Error"
        return if (value == floor(value) && abs(value) < 1_000_000_000.0) {
            value.toLong().toString()
        } else {
            "%.8f".format(value).trimEnd('0').trimEnd('.')
        }
    }
}
