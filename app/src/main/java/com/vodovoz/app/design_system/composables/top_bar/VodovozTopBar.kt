package com.vodovoz.app.design_system.composables.top_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.ClickableIcon

@Composable
fun VodovozTopBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    title: String,
    actionPainter: Painter? = null,
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ClickableIcon(
            modifier = Modifier.clip(CircleShape),
            painter = painterResource(id = R.drawable.ic_arrow_left),
            tint = MaterialTheme.colorScheme.onBackground,
            onClick = onBack
        )
        Text(
            text = title,
            modifier = Modifier.weight(1f).padding(start = 32.dp, end = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        actionPainter?.let {
            ClickableIcon(
                modifier = Modifier.clip(MaterialTheme.shapes.small),
                painter = actionPainter,
                tint = MaterialTheme.colorScheme.onBackground,
                onClick = onActionClick
            )
        }
    }
}