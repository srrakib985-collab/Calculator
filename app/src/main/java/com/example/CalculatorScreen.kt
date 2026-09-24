package com.example

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CalcBackground
import com.example.ui.theme.CalcBtnClear
import com.example.ui.theme.CalcBtnEqual
import com.example.ui.theme.CalcBtnNumber
import com.example.ui.theme.CalcBtnOperator
import com.example.ui.theme.CalcCardSurface
import com.example.ui.theme.CalcDisplaySurface
import com.example.ui.theme.CalcTextPrimary
import com.example.ui.theme.CalcTextSecondary

@Composable
fun CalculatorScreen(
  viewModel: CalculatorViewModel,
  modifier: Modifier = Modifier,
) {
  val display by viewModel.display.collectAsStateWithLifecycle()
  val previousEquation by viewModel.previousEquation.collectAsStateWithLifecycle()

  Surface(
    modifier = modifier
      .fillMaxSize()
      .background(CalcBackground),
    color = CalcBackground,
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .safeDrawingPadding()
        .padding(16.dp),
      contentAlignment = Alignment.Center,
    ) {
      CalculatorCard(
        display = display,
        previousEquation = previousEquation,
        onNumberClick = viewModel::onNumberClick,
        onOperatorClick = viewModel::onOperatorClick,
        onClearClick = viewModel::onClearClick,
        onDeleteClick = viewModel::onDeleteClick,
        onPercentClick = viewModel::onPercentClick,
        onEqualClick = viewModel::onEqualClick,
      )
    }
  }
}

@Composable
fun CalculatorCard(
  display: String,
  previousEquation: String,
  onNumberClick: (String) -> Unit,
  onOperatorClick: (String) -> Unit,
  onClearClick: () -> Unit,
  onDeleteClick: () -> Unit,
  onPercentClick: () -> Unit,
  onEqualClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier
      .widthIn(max = 380.dp)
      .fillMaxWidth()
      .shadow(
        elevation = 20.dp,
        shape = RoundedCornerShape(25.dp),
        ambientColor = Color.Black,
        spotColor = Color.Black,
      ),
    shape = RoundedCornerShape(25.dp),
    colors = CardDefaults.cardColors(containerColor = CalcCardSurface),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Display Area
      CalculatorDisplay(
        display = display,
        previousEquation = previousEquation,
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Buttons Keypad (4 columns grid)
      CalculatorKeypad(
        onNumberClick = onNumberClick,
        onOperatorClick = onOperatorClick,
        onClearClick = onClearClick,
        onDeleteClick = onDeleteClick,
        onPercentClick = onPercentClick,
        onEqualClick = onEqualClick,
      )
    }
  }
}

@Composable
fun CalculatorDisplay(
  display: String,
  previousEquation: String,
  modifier: Modifier = Modifier,
) {
  val scrollState = rememberScrollState()

  // Auto-scroll to end when text changes
  LaunchedEffect(display) {
    scrollState.animateScrollTo(scrollState.maxValue)
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(105.dp)
      .clip(RoundedCornerShape(15.dp))
      .background(CalcDisplaySurface)
      .padding(horizontal = 20.dp, vertical = 12.dp),
    contentAlignment = Alignment.BottomEnd,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.End,
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      // Previous Equation / History
      Text(
        text = previousEquation,
        color = CalcTextSecondary,
        fontSize = 15.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.End,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("equation_history"),
      )

      // Active Display Value
      val dynamicFontSize = when {
        display.length > 11 -> 26.sp
        display.length > 8 -> 32.sp
        else -> 38.sp
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = display,
          color = CalcTextPrimary,
          fontSize = dynamicFontSize,
          fontWeight = FontWeight.Normal,
          textAlign = TextAlign.End,
          maxLines = 1,
          fontFamily = FontFamily.SansSerif,
          modifier = Modifier.testTag("display"),
        )
      }
    }
  }
}

@Composable
fun CalculatorKeypad(
  onNumberClick: (String) -> Unit,
  onOperatorClick: (String) -> Unit,
  onClearClick: () -> Unit,
  onDeleteClick: () -> Unit,
  onPercentClick: () -> Unit,
  onEqualClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val gap = 12.dp

  BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
    // Total 4 columns and 3 gaps
    val columnWidth = (maxWidth - (gap * 3)) / 4
    val span2Width = (columnWidth * 2) + gap
    val buttonHeight = 65.dp

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(gap),
    ) {
      // Row 1: AC, ⌫, %, ÷
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gap),
      ) {
        CalcButton(
          text = "AC",
          backgroundColor = CalcBtnClear,
          contentDesc = "All Clear",
          testTag = "btn_ac",
          width = columnWidth,
          height = buttonHeight,
          onClick = onClearClick,
        )
        CalcButton(
          text = "⌫",
          backgroundColor = CalcBtnNumber,
          contentDesc = "Delete Last Digit",
          testTag = "btn_backspace",
          width = columnWidth,
          height = buttonHeight,
          onClick = onDeleteClick,
        )
        CalcButton(
          text = "%",
          backgroundColor = CalcBtnNumber,
          contentDesc = "Percent",
          testTag = "btn_percent",
          width = columnWidth,
          height = buttonHeight,
          onClick = onPercentClick,
        )
        CalcButton(
          text = "÷",
          backgroundColor = CalcBtnOperator,
          contentDesc = "Divide",
          testTag = "btn_divide",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onOperatorClick("÷") },
        )
      }

      // Row 2: 7, 8, 9, ×
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gap),
      ) {
        CalcButton(
          text = "7",
          backgroundColor = CalcBtnNumber,
          contentDesc = "7",
          testTag = "btn_7",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onNumberClick("7") },
        )
        CalcButton(
          text = "8",
          backgroundColor = CalcBtnNumber,
          contentDesc = "8",
          testTag = "btn_8",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onNumberClick("8") },
        )
        CalcButton(
          text = "9",
          backgroundColor = CalcBtnNumber,
          contentDesc = "9",
          testTag = "btn_9",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onNumberClick("9") },
        )
        CalcButton(
          text = "×",
          backgroundColor = CalcBtnOperator,
          contentDesc = "Multiply",
          testTag = "btn_multiply",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onOperatorClick("×") },
        )
      }

      // Row 3: 4, 5, 6, −
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gap),
      ) {
        CalcButton(
          text = "4",
          backgroundColor = CalcBtnNumber,
          contentDesc = "4",
          testTag = "btn_4",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onNumberClick("4") },
        )
        CalcButton(
          text = "5",
          backgroundColor = CalcBtnNumber,
          contentDesc = "5",
          testTag = "btn_5",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onNumberClick("5") },
        )
        CalcButton(
          text = "6",
          backgroundColor = CalcBtnNumber,
          contentDesc = "6",
          testTag = "btn_6",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onNumberClick("6") },
        )
        CalcButton(
          text = "−",
          backgroundColor = CalcBtnOperator,
          contentDesc = "Subtract",
          testTag = "btn_subtract",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onOperatorClick("−") },
        )
      }

      // Row 4: 1, 2, 3, +
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gap),
      ) {
        CalcButton(
          text = "1",
          backgroundColor = CalcBtnNumber,
          contentDesc = "1",
          testTag = "btn_1",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onNumberClick("1") },
        )
        CalcButton(
          text = "2",
          backgroundColor = CalcBtnNumber,
          contentDesc = "2",
          testTag = "btn_2",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onNumberClick("2") },
        )
        CalcButton(
          text = "3",
          backgroundColor = CalcBtnNumber,
          contentDesc = "3",
          testTag = "btn_3",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onNumberClick("3") },
        )
        CalcButton(
          text = "+",
          backgroundColor = CalcBtnOperator,
          contentDesc = "Add",
          testTag = "btn_add",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onOperatorClick("+") },
        )
      }

      // Row 5: 0 (span 2), ., =
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gap),
      ) {
        CalcButton(
          text = "0",
          backgroundColor = CalcBtnNumber,
          contentDesc = "0",
          testTag = "btn_0",
          width = span2Width,
          height = buttonHeight,
          onClick = { onNumberClick("0") },
        )
        CalcButton(
          text = ".",
          backgroundColor = CalcBtnNumber,
          contentDesc = "Decimal point",
          testTag = "btn_decimal",
          width = columnWidth,
          height = buttonHeight,
          onClick = { onNumberClick(".") },
        )
        CalcButton(
          text = "=",
          backgroundColor = CalcBtnEqual,
          contentDesc = "Calculate equals",
          testTag = "btn_equal",
          width = columnWidth,
          height = buttonHeight,
          onClick = onEqualClick,
        )
      }
    }
  }
}

@Composable
fun CalcButton(
  text: String,
  backgroundColor: Color,
  contentDesc: String,
  testTag: String,
  width: Dp,
  height: Dp,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val haptic = LocalHapticFeedback.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  // Match CSS active scale transform: button:active { transform: scale(.95); }
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.94f else 1.0f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMedium,
    ),
    label = "calc_btn_scale",
  )

  Box(
    modifier = modifier
      .size(width = width, height = height)
      .scale(scale)
      .clip(RoundedCornerShape(18.dp))
      .background(backgroundColor)
      .clickable(
        interactionSource = interactionSource,
        indication = ripple(color = Color.White.copy(alpha = 0.3f)),
        role = Role.Button,
        onClick = {
          haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
          onClick()
        },
      )
      .semantics {
        contentDescription = contentDesc
      }
      .testTag(testTag),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = text,
      color = CalcTextPrimary,
      fontSize = 23.sp,
      fontWeight = FontWeight.Normal,
      fontFamily = FontFamily.SansSerif,
      textAlign = TextAlign.Center,
    )
  }
}
