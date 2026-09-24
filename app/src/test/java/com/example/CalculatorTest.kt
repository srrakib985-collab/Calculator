package com.example

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorTest {

  @Test
  fun testBasicAddition() {
    val result = CalculatorEvaluator.evaluate("12+8")
    assertEquals("20", result)
  }

  @Test
  fun testBasicSubtraction() {
    val result = CalculatorEvaluator.evaluate("15−7")
    assertEquals("8", result)
  }

  @Test
  fun testMultiplicationAndDivisionPrecedence() {
    // 2 + 3 * 4 should be 14, not 20
    val result = CalculatorEvaluator.evaluate("2+3×4")
    assertEquals("14", result)

    // 10 - 6 / 2 should be 7
    val result2 = CalculatorEvaluator.evaluate("10−6÷2")
    assertEquals("7", result2)
  }

  @Test
  fun testDecimalOperations() {
    val result = CalculatorEvaluator.evaluate("0.1+0.2")
    assertEquals("0.3", result)
  }

  @Test
  fun testDivisionByZero() {
    val result = CalculatorEvaluator.evaluate("5÷0")
    assertEquals("Error", result)
  }

  @Test
  fun testTrailingOperatorTrim() {
    val result = CalculatorEvaluator.evaluate("9×")
    assertEquals("9", result)
  }

  @Test
  fun testPercent() {
    val result = CalculatorEvaluator.evaluatePercent("200")
    assertEquals("2", result)

    val result2 = CalculatorEvaluator.evaluatePercent("50")
    assertEquals("0.5", result2)
  }

  @Test
  fun testViewModelOperations() {
    val vm = CalculatorViewModel()
    assertEquals("0", vm.display.value)

    vm.onNumberClick("5")
    assertEquals("5", vm.display.value)

    vm.onOperatorClick("+")
    assertEquals("5+", vm.display.value)

    vm.onNumberClick("3")
    assertEquals("5+3", vm.display.value)

    vm.onEqualClick()
    assertEquals("8", vm.display.value)
    assertEquals("5+3 =", vm.previousEquation.value)

    vm.onDeleteClick()
    assertEquals("0", vm.display.value)

    vm.onNumberClick("9")
    vm.onPercentClick()
    assertEquals("0.09", vm.display.value)

    vm.onClearClick()
    assertEquals("0", vm.display.value)
    assertEquals("", vm.previousEquation.value)
  }
}
