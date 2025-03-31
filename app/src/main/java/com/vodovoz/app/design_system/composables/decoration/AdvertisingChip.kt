package com.vodovoz.app.design_system.composables.decoration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.ExtendedTheme

@Composable
fun AdvertisingChip(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .height(16.dp)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.surface.copy(0.7f))
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        val labelExtraSmall = ExtendedTheme.typography.labelExtraSmall
        Text(
            text = stringResource(R.string.advertising),
            color = MaterialTheme.colorScheme.onBackground,
            style = labelExtraSmall.copy(lineHeight = labelExtraSmall.fontSize),
            modifier = Modifier.padding(horizontal = 5.dp)
        )
    }
}