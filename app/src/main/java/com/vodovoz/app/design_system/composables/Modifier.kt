package com.vodovoz.app.design_system.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned

fun Modifier.isElementVisible(onVisibilityChanged: (Boolean) -> Unit) = composed {
    val isVisible by remember { derivedStateOf { mutableStateOf(false) } }
    LaunchedEffect(isVisible.value) { onVisibilityChanged.invoke(isVisible.value) }
    this.onGloballyPositioned { layoutCoordinates ->
        isVisible.value = layoutCoordinates.parentLayoutCoordinates?.let {
            val parentBounds = it.boundsInWindow()
            val childBounds = layoutCoordinates.boundsInWindow()
            parentBounds.overlaps(childBounds)
        } ?: false
    }
}

@Stable
fun Modifier.vodovozSurface(
    shape: Shape,
    backgroundColor: Color,
    border: BorderStroke?,
    shadowElevation: Float,
) = this
    .graphicsLayer(shadowElevation = shadowElevation, shape = shape, clip = false)
    .then(if (border != null) Modifier.border(border, shape) else Modifier)
    .background(color = backgroundColor, shape = shape)
    .clip(shape)