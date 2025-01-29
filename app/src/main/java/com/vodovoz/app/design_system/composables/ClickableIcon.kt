package com.vodovoz.app.design_system.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R

@Composable
fun ClickableIcon(painter: Painter, tint: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Icon(
        painter = painter,
        contentDescription = null,
        modifier = modifier
            .size(24.dp)
            .clip(RoundedCornerShape(1.dp))
            .clickable(
                onClick = { onClick() },
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ),
        tint = tint
    )
}