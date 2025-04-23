package com.vodovoz.app.feature.all.orders.detail.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R

@Composable
fun OrderDetailsTopBar(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    onCopy: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = onBack),
            tint = MaterialTheme.colorScheme.onBackground
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 32.dp, end = 16.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_copy),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = onCopy),
            tint = MaterialTheme.colorScheme.onBackground
        )

    }
}