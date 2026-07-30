package com.calculadora

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * CONCEPTO: Unit Testing con JUnit 4
 * ─────────────────────────────────────────────────────────────────────────────
 * Los tests unitarios verifican que unidades individuales de código
 * funcionen correctamente de forma aislada.
 *
 * Ventajas:
 *  - Corren directamente en la JVM (sin emulador) → muy rápidos
 *  - No dependen de Android Framework
 *  - Documentan el comportamiento esperado del código
 *
 * CONCEPTO: @Before
 * Se ejecuta antes de CADA test. Aquí inicializamos el ViewModel.
 *
 * CONCEPTO: @Test
 * Marca un método como caso de prueba.
 * El compilador lo ejecuta automáticamente al correr el suite de tests.
 *
 * CONCEPTO: assertEquals(expected, actual)
 * Verifica que dos valores sean iguales.
 * Si no lo son, el test falla con un mensaje descriptivo.
 *
 * NOTA: Usamos backtick-strings `nombre del test` para nombres legibles
 * que describen exactamente el comportamiento verificado.
 */
class CalculatorViewModelTest {

    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setUp() {
        viewModel = CalculatorViewModel()
    }

    // ── Estado inicial ───────────────────────────────────────────────────────

    @Test
    fun `estado inicial muestra cero`() {
        assertEquals("0", viewModel.state.value.displayValue)
        assertNull(viewModel.state.value.firstOperand)
        assertNull(viewModel.state.value.pendingOperation)
    }

    // ── Ingreso de números ───────────────────────────────────────────────────

    @Test
    fun `ingresar un digito actualiza pantalla`() {
        viewModel.onAction(CalculatorAction.Number(5))
        assertEquals("5", viewModel.state.value.displayValue)
    }

    @Test
    fun `ingresar multiples digitos construye el numero`() {
        viewModel.onAction(CalculatorAction.Number(1))
        viewModel.onAction(CalculatorAction.Number(2))
        viewModel.onAction(CalculatorAction.Number(3))
        assertEquals("123", viewModel.state.value.displayValue)
    }

    @Test
    fun `no muestra cero inicial al escribir un numero`() {
        viewModel.onAction(CalculatorAction.Number(5))
        assertEquals("5", viewModel.state.value.displayValue)
    }

    @Test
    fun `limite de 9 digitos se respeta`() {
        repeat(10) { viewModel.onAction(CalculatorAction.Number(1)) }
        assertEquals(9, viewModel.state.value.displayValue.length)
    }

    // ── Punto decimal ────────────────────────────────────────────────────────

    @Test
    fun `punto decimal se agrega al numero`() {
        viewModel.onAction(CalculatorAction.Number(3))
        viewModel.onAction(CalculatorAction.Decimal)
        viewModel.onAction(CalculatorAction.Number(1))
        viewModel.onAction(CalculatorAction.Number(4))
        assertEquals("3.14", viewModel.state.value.displayValue)
    }

    @Test
    fun `segundo punto decimal se ignora`() {
        viewModel.onAction(CalculatorAction.Number(3))
        viewModel.onAction(CalculatorAction.Decimal)
        viewModel.onAction(CalculatorAction.Decimal) // duplicado → ignorado
        viewModel.onAction(CalculatorAction.Number(5))
        assertEquals("3.5", viewModel.state.value.displayValue)
    }

    @Test
    fun `punto decimal al inicio produce 0 punto`() {
        viewModel.onAction(CalculatorAction.Decimal)
        assertEquals("0.", viewModel.state.value.displayValue)
    }

    // ── Operaciones aritméticas ──────────────────────────────────────────────

    @Test
    fun `suma funciona correctamente`() {
        viewModel.onAction(CalculatorAction.Number(3))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.ADD))
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.Calculate)
        assertEquals("8", viewModel.state.value.displayValue)
    }

    @Test
    fun `resta funciona correctamente`() {
        viewModel.onAction(CalculatorAction.Number(1))
        viewModel.onAction(CalculatorAction.Number(0))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.SUBTRACT))
        viewModel.onAction(CalculatorAction.Number(3))
        viewModel.onAction(CalculatorAction.Calculate)
        assertEquals("7", viewModel.state.value.displayValue)
    }

    @Test
    fun `multiplicacion funciona correctamente`() {
        viewModel.onAction(CalculatorAction.Number(6))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.MULTIPLY))
        viewModel.onAction(CalculatorAction.Number(7))
        viewModel.onAction(CalculatorAction.Calculate)
        assertEquals("42", viewModel.state.value.displayValue)
    }

    @Test
    fun `division funciona correctamente`() {
        viewModel.onAction(CalculatorAction.Number(1))
        viewModel.onAction(CalculatorAction.Number(0))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.DIVIDE))
        viewModel.onAction(CalculatorAction.Number(2))
        viewModel.onAction(CalculatorAction.Calculate)
        assertEquals("5", viewModel.state.value.displayValue)
    }

    @Test
    fun `division por cero muestra Error`() {
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.DIVIDE))
        viewModel.onAction(CalculatorAction.Number(0))
        viewModel.onAction(CalculatorAction.Calculate)
        assertEquals("Error", viewModel.state.value.displayValue)
    }

    @Test
    fun `operaciones encadenadas calculan resultado intermedio`() {
        // 2 + 3 + 4 = 9
        viewModel.onAction(CalculatorAction.Number(2))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.ADD))
        viewModel.onAction(CalculatorAction.Number(3))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.ADD)) // calcula 2+3=5
        viewModel.onAction(CalculatorAction.Number(4))
        viewModel.onAction(CalculatorAction.Calculate)
        assertEquals("9", viewModel.state.value.displayValue)
    }

    @Test
    fun `decimales en operaciones`() {
        viewModel.onAction(CalculatorAction.Number(1))
        viewModel.onAction(CalculatorAction.Decimal)
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.ADD))
        viewModel.onAction(CalculatorAction.Number(1))
        viewModel.onAction(CalculatorAction.Decimal)
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.Calculate)
        assertEquals("3", viewModel.state.value.displayValue)
    }

    // ── Clear ────────────────────────────────────────────────────────────────

    @Test
    fun `clear restablece estado inicial`() {
        viewModel.onAction(CalculatorAction.Number(9))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.ADD))
        viewModel.onAction(CalculatorAction.Clear)

        assertEquals("0", viewModel.state.value.displayValue)
        assertNull(viewModel.state.value.firstOperand)
        assertNull(viewModel.state.value.pendingOperation)
    }

    // ── Cambio de signo (+/-) ────────────────────────────────────────────────

    @Test
    fun `cambio de signo convierte positivo en negativo`() {
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.ToggleSign)
        assertEquals("-5", viewModel.state.value.displayValue)
    }

    @Test
    fun `doble cambio de signo vuelve al positivo`() {
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.ToggleSign)
        viewModel.onAction(CalculatorAction.ToggleSign)
        assertEquals("5", viewModel.state.value.displayValue)
    }

    // ── Porcentaje (%) ───────────────────────────────────────────────────────

    @Test
    fun `porcentaje divide por 100`() {
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.Number(0))
        viewModel.onAction(CalculatorAction.Percent)
        assertEquals("0.5", viewModel.state.value.displayValue)
    }

    @Test
    fun `porcentaje de numero entero sin decimales`() {
        viewModel.onAction(CalculatorAction.Number(1))
        viewModel.onAction(CalculatorAction.Number(0))
        viewModel.onAction(CalculatorAction.Number(0))
        viewModel.onAction(CalculatorAction.Percent)
        assertEquals("1", viewModel.state.value.displayValue)
    }

    // ── formatNumber ─────────────────────────────────────────────────────────

    @Test
    fun `formatNumber muestra entero sin decimales`() {
        assertEquals("5", viewModel.formatNumber(5.0))
        assertEquals("100", viewModel.formatNumber(100.0))
        assertEquals("-7", viewModel.formatNumber(-7.0))
        assertEquals("0", viewModel.formatNumber(0.0))
    }

    @Test
    fun `formatNumber muestra Error para NaN`() {
        assertEquals("Error", viewModel.formatNumber(Double.NaN))
    }

    @Test
    fun `formatNumber muestra Error para Infinito`() {
        assertEquals("Error", viewModel.formatNumber(Double.POSITIVE_INFINITY))
        assertEquals("Error", viewModel.formatNumber(Double.NEGATIVE_INFINITY))
    }

    @Test
    fun `formatNumber elimina ceros decimales finales`() {
        assertEquals("3.14", viewModel.formatNumber(3.14))
        assertEquals("0.5", viewModel.formatNumber(0.5))
    }
}
