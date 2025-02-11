package com.vodovoz.app.design_system.composables.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VodovozOutlinedCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    containerColor: Color = MaterialTheme.colorScheme.background,
    shape: CornerBasedShape = MaterialTheme.shapes.large,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {

    if (onClick == null) {
        OutlinedCard(
            modifier = modifier
                .fillMaxWidth(),
            elevation = CardDefaults.outlinedCardElevation(0.dp),
            shape = shape,
            border = BorderStroke(1.dp, borderColor),
            colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
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
            shape = shape,
            border = BorderStroke(1.dp, borderColor),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.background),
            onClick = onClick
        ) {
            Column(modifier = Modifier.padding(contentPadding)) {
                content()
            }
        }
    }
}