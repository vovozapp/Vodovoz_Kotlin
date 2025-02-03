package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HomeRow(modifier: Modifier = Modifier, content: @Composable (itemWidth: Dp) -> Unit) {
    val horizontalPadding = 16.dp
    val space = 8.dp
    val productCardWidth =
        (LocalConfiguration.current.screenWidthDp.dp - horizontalPadding * 2 - space) / 2

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = horizontalPadding)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space)
    ) {
        content(productCardWidth)
    }
}