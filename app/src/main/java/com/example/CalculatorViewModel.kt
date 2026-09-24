package com.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {
  private val _display = MutableStateFlow("0")
  val display: StateFlow<String> = _display.asStateFlow()

  private val _previousEquation = MutableStateFlow("")
  val previousEquation: StateFlow<String> = _previousEquation.asStateFlow()

  private val operatorChars = listOf('+', '−', '×', '÷')

  fun onNumberClick(number: String) {
    val current = _display.value
    if (current == "Error") {
      _display.value = if (number == ".") "0." else number
      _previousEquation.value = ""
      return
    }

    if (number == ".") {
      val lastOperand = current.split('+', '−', '×', '÷').lastOrNull() ?: ""
      if (lastOperand.contains('.')) {
        return // Avoid multiple dots in same number
      }
      if (current.isEmpty() || current.last() in operatorChars) {
        _display.value = current + "0."
        return
      }
    }

    if (current == "0" && number != ".") {
      _display.value = number
    } else {
      _display.value = current + number
    }
  }

  fun onOperatorClick(operator: String) {
    val current = _display.value
    if (current == "Error") {
      _display.value = "0$operator"
      _previousEquation.value = ""
      return
    }

    if (current.isNotEmpty() && current.last() in operatorChars) {
      // Replace last operator
      _display.value = current.dropLast(1) + operator
    } else {
      _display.value = current + operator
    }
  }

  fun onClearClick() {
    _display.value = "0"
    _previousEquation.value = ""
  }

  fun onDeleteClick() {
    val current = _display.value
    if (current == "Error" || current.length <= 1) {
      _display.value = "0"
    } else {
      _display.value = current.dropLast(1)
    }
  }

  fun onPercentClick() {
    val current = _display.value
    if (current == "Error") return
    val result = CalculatorEvaluator.evaluatePercent(current)
    _previousEquation.value = "$current %"
    _display.value = result
  }

  fun onEqualClick() {
    val current = _display.value
    if (current == "Error") return
    val result = CalculatorEvaluator.evaluate(current)
    _previousEquation.value = "$current ="
    _display.value = result
  }
}
