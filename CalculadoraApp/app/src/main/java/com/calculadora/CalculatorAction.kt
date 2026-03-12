package com.calculadora

/**
 * CONCEPTO: Enum Class
 * ─────────────────────────────────────────────────────────────────────────────
 * Un enum define un conjunto cerrado y fijo de constantes con nombre.
 * Aquí representamos las 4 operaciones aritméticas básicas de la calculadora.
 * Cada constante lleva su símbolo visual para mostrarlo en la pantalla.
 *
 * Ventaja: imposible crear una operación inválida (type-safe).
 */
enum class CalculatorOperation(val symbol: String) {
    ADD("+"),
    SUBTRACT("−"),
    MULTIPLY("×"),
    DIVIDE("÷")
}

/**
 * CONCEPTO: Sealed Class (Clase sellada)
 * ─────────────────────────────────────────────────────────────────────────────
 * Una sealed class define una jerarquía cerrada de tipos. Todos sus subtipos
 * deben declararse en el mismo archivo/paquete.
 *
 * Aquí la usamos para representar TODAS las acciones posibles del usuario:
 *  - Presionar un número
 *  - Seleccionar una operación
 *  - Presionar =, AC, +/-, %, .
 *
 * Beneficio clave: al usar `when` con una sealed class, el compilador exige
 * cubrir TODOS los casos → menos errores en tiempo de ejecución.
 *
 * data class: para subtipos que llevan datos (número pulsado, operación elegida)
 * object:     para subtipos sin datos (acciones únicas como Clear, Decimal)
 */
sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    data class Operation(val operation: CalculatorOperation) : CalculatorAction()
    data object Calculate : CalculatorAction()
    data object Clear : CalculatorAction()
    data object Decimal : CalculatorAction()
    data object ToggleSign : CalculatorAction()
    data object Percent : CalculatorAction()
}

/**
 * CONCEPTO: Data Class
 * ─────────────────────────────────────────────────────────────────────────────
 * Una data class genera automáticamente:
 *  - equals() / hashCode() — comparación por valor, no por referencia
 *  - toString() — representación legible del objeto
 *  - copy() — crear una copia modificando solo algunos campos (inmutabilidad)
 *
 * CalculatorState es el estado completo de la UI en un momento dado.
 * Siguiendo el patrón UDF (Unidirectional Data Flow), la UI solo lee este
 * estado y envía acciones al ViewModel; nunca modifica el estado directamente.
 *
 * Campos:
 *  displayValue    — número visible en pantalla
 *  firstOperand    — primer número almacenado al pulsar una operación
 *  pendingOperation — operación elegida (+, −, ×, ÷) pendiente de calcular
 *  isNewEntry      — true cuando el próximo dígito inicia un número nuevo
 *  justCalculated  — true justo después de pulsar "="
 */
data class CalculatorState(
    val displayValue: String = "0",
    val firstOperand: Double? = null,
    val pendingOperation: CalculatorOperation? = null,
    val isNewEntry: Boolean = true,
    val justCalculated: Boolean = false
)
