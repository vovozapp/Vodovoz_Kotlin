package com.vodovoz.app.design_system.composables.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.ExtendedTheme
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.vodovozSurface

@Composable
fun VodovozChip(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onSelect: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
) {
    Box(
        modifier = modifier
            .vodovozSurface(
                border = if (selected) null else BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = MaterialTheme.shapes.small,
                backgroundColor = if (selected) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.surface,
                shadowElevation = 0f
            )
            .clickable(
                interactionSource = null,
                indication = ripple(),
                enabled = true,
                onClick = onSelect
            )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(contentPadding),
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun VodovozColorChip(modifier: Modifier = Modifier, color: Color, text: String) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
fun VodovozColorChipSmall(modifier: Modifier = Modifier, color: Color, text: String) {
    Box(
        modifier = modifier
            .widthIn(30.dp)
            .clip(MaterialTheme.shapes.small)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 5.dp),
            style = ExtendedTheme.typography.labelExtraSmall,
            color = MaterialTheme.colorScheme.background
        )
    }
}


@Preview
@Composable
private fun VodovozChipPreview() {
    VodovozTheme {
        VodovozChip(text = "Hello world!", selected = true)
    }
}