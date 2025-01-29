package com.vodovoz.app.design_system.composables.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VodovozOutlinedCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {

    if (onClick == null) {
        OutlinedCard(
            modifier = modifier
                .fillMaxWidth(),
            elevation = CardDefaults.outlinedCardElevation(0.dp),
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.background),
        ) {
            Column(modifier = Modifier.padding(contentPadding)) {
                content()
            }
        }
    } else {
        OutlinedCard(
            modifier = modifier
                .fillMaxWidth(),
            elevation = CardDefaults.outlinedCardElevation(0.dp),
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.background),
            onClick = onClick
        ) {
            Column(modifier = Modifier.padding(contentPadding)) {
                content()
            }
        }
    }
}