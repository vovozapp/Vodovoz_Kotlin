package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.model.LabelWithColorUi


@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsLabels(
    modifier: Modifier = Modifier,
    labels: List<LabelWithColorUi>,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEach { labelWithColor ->
            Surface(
                contentColor = MaterialTheme.colorScheme.background,
                color = labelWithColor.color,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(modifier = Modifier.padding(horizontal = 8.dp),text = labelWithColor.name, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

