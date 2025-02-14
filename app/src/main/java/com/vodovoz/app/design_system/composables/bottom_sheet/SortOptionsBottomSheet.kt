package com.vodovoz.app.design_system.composables.bottom_sheet

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.vodovoz.app.feature.product_comments.model.SortUi
import kotlinx.coroutines.delay

@Suppress("NonSkippableComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortOptionsBottomSheet(
    onDismissRequest: () -> Unit,
    state: SheetState = rememberModalBottomSheetState(true),
    currentSort: SortUi,
    sorting: List<SortUi>,
    onSortSelect: (SortUi) -> Unit,
) {
    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onDismissRequest,
        dragHandle = {
            VodovozDragHandle()
        },
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
        ) {
            sorting.forEachIndexed { index, sort ->
                SortItem(sort = sort, selected = currentSort == sort, onSelect = onSortSelect)

                if (index != sorting.lastIndex) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SortItem(
    modifier: Modifier = Modifier,
    sort: SortUi,
    selected: Boolean,
    onSelect: (SortUi) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect(sort) }
            .padding(horizontal = 16.dp)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = sort.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(16.dp))
        SortRadioButton(selected = selected, onClick = { onSelect(sort) })
    }
}

@Composable
private fun SortRadioButton(
    selected: Boolean,
    onClick: (() -> Unit),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val dotRadius =
        animateDpAsState(
            targetValue = if (selected) 14.dp / 2 else 0.dp,
            animationSpec = tween(durationMillis = 120, easing = LinearEasing), label = "dotRadius"
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
                .fillMaxSize()
                .background(Color.White)
        ) {
            SortRadioButton(
                selected = selected,
                onClick = { selected = !selected }
            )
        }
    }
}
