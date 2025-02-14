package com.vodovoz.app.design_system.composables.button

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import kotlinx.coroutines.delay

@Composable
fun LayoutSwitchButton(
    modifier: Modifier = Modifier,
    isGridView: Boolean,
    onSwitch: (Boolean) -> Unit,
) {
    Crossfade(
        targetState = isGridView,
        label = "LayoutSwitchAnimation",
        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
    ) { state ->
        Icon(
            painter = painterResource(id = if (!state) R.drawable.ic_grid_view_24 else R.drawable.ic_linear_view_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = modifier
                .size(24.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable { onSwitch(!state) }
        )
    }
}

@Preview
@Composable
private fun LayoutSwitchButtonPreview() {
    var isGridView by remember { mutableStateOf(true) }

    VodovozTheme {

        LayoutSwitchButton(
            modifier = Modifier.background(Color.White),
            isGridView = isGridView,
            onSwitch = { isGridView = it }
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000L)
            isGridView = !isGridView
        }
    }

}