package com.vodovoz.app.design_system.composables.slider

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.VodovozTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VodovozRangeSlider(modifier: Modifier = Modifier) {
    val rangeSliderState = remember {
        RangeSliderState(
            0f,
            100f,
            valueRange = 0f..100f,
            onValueChangeFinished = {

            }
        )
    }
    val startInteractionSource = remember { MutableInteractionSource() }
    val endInteractionSource = remember { MutableInteractionSource() }
    val startThumbAndTrackColors =
        SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    val endThumbColors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        RangeSlider(
            modifier = modifier.requiredHeight(48.dp),
            state = rangeSliderState,
            startInteractionSource = startInteractionSource,
            endInteractionSource = endInteractionSource,
            startThumb = {
                VodovozThumb()
            },
            endThumb = {
                VodovozThumb()
            },
            track = { rangeSliderState ->
                SliderDefaults.Track(
                    modifier = Modifier.requiredHeight(4.dp),
                    colors = startThumbAndTrackColors,
                    enabled = true,
                    rangeSliderState = rangeSliderState,
                    drawStopIndicator = { },
                    thumbTrackGapSize = 0.dp
                )
            }
        )
    }
}

@Composable
private fun VodovozThumb(modifier: Modifier = Modifier) {
    IconButton(
        modifier = modifier,
        onClick = {},
        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .shadow(5.dp, shape = CircleShape)
                .background(MaterialTheme.colorScheme.primary, CircleShape)

        )
    }
}

@Preview
@Composable
private fun VodovozRangeSliderPreview() {
    VodovozTheme {

        VodovozRangeSlider(modifier = Modifier.background(MaterialTheme.colorScheme.background))
    }
}