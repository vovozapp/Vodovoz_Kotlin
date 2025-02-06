package com.vodovoz.app.feature.full_screen_history_slider.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.VodovozTheme

@Composable
fun StoriesIndicator(
    modifier: Modifier = Modifier,
    countPages: Int,
    pageIndex: Int,
    progress: Float,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        repeat(countPages) {
            PageIndicator(
                modifier = Modifier.weight(1f),
                progress = { if (pageIndex == it) progress else if (it > pageIndex) 0f else 1f }
            )
        }
    }
}

@Preview
@Composable
private fun StoriesIndicatorPreview() {
    VodovozTheme {
        StoriesIndicator(countPages = 3, pageIndex = 1, progress = 0.5f)
    }
}

@Composable
private fun PageIndicator(
    modifier: Modifier,
    progress: () -> Float,
) {

    LinearProgressIndicator(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(0.8f),
        modifier = modifier
            .height(2.dp)
            .clip(RoundedCornerShape(1.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.25f)),
        progress = { progress() },
        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.25f),
        gapSize = 0.dp,
        drawStopIndicator = { }
    )

}