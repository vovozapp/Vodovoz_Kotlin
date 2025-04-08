package com.vodovoz.app.design_system.composables.button

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.VodovozTheme
import kotlinx.coroutines.delay

@Composable
fun VodovozRadioButton(
    selected: Boolean,
    onClick: (() -> Unit),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val dotRadius = animateDpAsState(
            targetValue = if (selected) 14.dp / 2 else 0.dp,
            animationSpec = tween(durationMillis = 90, easing = LinearEasing), label = "dotRadius"
        )
    val selectableModifier = Modifier.selectable(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        role = Role.RadioButton,
        interactionSource = interactionSource,
        indication =
        ripple(
            bounded = false,
            radius = 10.dp
        )
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline

    Canvas(
        modifier
            .then(selectableModifier)
            .size(20.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        val strokeWidth = 2.dp.toPx()

        drawCircle(
            color = if (selected) primaryColor else outlineColor,
            radius = (20.dp / 2).toPx() - strokeWidth / 2,
            style = Stroke(strokeWidth)
        )

        if (dotRadius.value > 0.dp) {
            drawCircle(primaryColor, dotRadius.value.toPx() - strokeWidth / 2, style = Fill)
        }
    }
}

@Composable
fun VodovozRadioRow(
    modifier: Modifier = Modifier,
    name: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(horizontal = 16.dp)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(16.dp))
        VodovozRadioButton(selected = selected, onClick = { onSelect() })
    }
}


@Preview
@Composable
private fun SortRadioButtonPreview() {
    var selected by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000L)
            selected = !selected
        }
    }

    VodovozTheme {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            VodovozRadioButton(
                selected = selected,
                onClick = { selected = !selected }
            )
        }
    }
}
