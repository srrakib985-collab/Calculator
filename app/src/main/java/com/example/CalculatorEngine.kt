package com.example

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object CalculatorEvaluator {
  private val mathContext = MathContext(12, RoundingMode.HALF_UP)

  fun evaluate(expression: String): String {
    try {
      val sanitized = expression
        .replace('×', '*')
        .replace('÷', '/')
        .replace('−', '-')
        .trim()

      if (sanitized.isEmpty() || sanitized == "Error") return "0"

      // Trim any trailing operator
      val cleanExpr = sanitized.trimEnd('+', '-', '*', '/')
      if (cleanExpr.isEmpty()) return "0"

      // Tokenize
      val tokens = tokenize(cleanExpr)
      if (tokens.isEmpty()) return "0"
      if (tokens.size == 1 && tokens[0] is BigDecimal) {
        return formatResult(tokens[0] as BigDecimal)
      }

      // Phase 1: handle * and /
      val step1Tokens = mutableListOf<Any>()
      var i = 0
      while (i < tokens.size) {
        val token = tokens[i]
        if (token is Char && (token == '*' || token == '/')) {
          if (step1Tokens.isEmpty() || i + 1 >= tokens.size) return "Error"
          val prev = step1Tokens.removeAt(step1Tokens.size - 1) as? BigDecimal ?: return "Error"
          val next = tokens[i + 1] as? BigDecimal ?: return "Error"
          val result = if (token == '*') {
            prev.multiply(next, mathContext)
          } else {
            if (next.compareTo(BigDecimal.ZERO) == 0) {
              return "Error"
            }
            prev.divide(next, mathContext)
          }
          step1Tokens.add(result)
          i += 2
        } else {
          step1Tokens.add(token)
          i++
        }
      }

      // Phase 2: handle + and -
      if (step1Tokens.isEmpty()) return "0"
      var result = step1Tokens[0] as? BigDecimal ?: return "Error"
      var j = 1
      while (j < step1Tokens.size) {
        val op = step1Tokens[j] as? Char ?: return "Error"
        if (j + 1 >= step1Tokens.size) return "Error"
        val next = step1Tokens[j + 1] as? BigDecimal ?: return "Error"
        result = if (op == '+') {
          result.add(next, mathContext)
        } else {
          result.subtract(next, mathContext)
        }
        j += 2
      }

      return formatResult(result)
    } catch (e: Exception) {
      return "Error"
    }
  }

  fun evaluatePercent(expression: String): String {
    val eval = evaluate(expression)
    if (eval == "Error") return "Error"
    return try {
      val bd = BigDecimal(eval)
      val res = bd.divide(BigDecimal(100), mathContext)
      formatResult(res)
    } catch (e: Exception) {
      "Error"
    }
  }

  private fun tokenize(expr: String): List<Any> {
    val tokens = mutableListOf<Any>()
    var numBuffer = StringBuilder()
    var i = 0
    while (i < expr.length) {
      val c = expr[i]
      if (c.isDigit() || c == '.') {
        numBuffer.append(c)
      } else if (c in listOf('+', '-', '*', '/')) {
        // Check if negative sign belongs to a number
        if (c == '-' && (i == 0 || expr[i - 1] in listOf('+', '-', '*', '/')) && numBuffer.isEmpty()) {
          numBuffer.append(c)
        } else {
          if (numBuffer.isNotEmpty()) {
            val numStr = numBuffer.toString()
            if (numStr == "-" || numStr == ".") return emptyList()
            tokens.add(BigDecimal(numStr))
            numBuffer = StringBuilder()
          }
          tokens.add(c)
        }
      }
      i++
    }
    if (numBuffer.isNotEmpty()) {
      val numStr = numBuffer.toString()
      if (numStr == "-" || numStr == ".") return emptyList()
      tokens.add(BigDecimal(numStr))
    }
    return tokens
  }

  private fun formatResult(value: BigDecimal): String {
    val stripped = value.stripTrailingZeros()
    var plain = stripped.toPlainString()
    // Avoid "-0"
    if (plain == "-0" || plain == "-0.0") plain = "0"
    return plain
  }
}
