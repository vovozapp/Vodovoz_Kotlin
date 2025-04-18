package com.vodovoz.app.design_system.composables.floating

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vodovoz.app.feature.home.composables.dropShadow

@Composable
fun BottomFloatingContainer(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val buttonShape = RoundedCornerShape(
        topStart = 12.dp,
        topEnd = 12.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(buttonShape, Color.Black.copy(0.14f), 14.dp, 3.dp)
            .dropShadow(buttonShape, Color.Black.copy(0.14f), 10.dp, 4.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = buttonShape
            )
            .padding(top = 10.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}