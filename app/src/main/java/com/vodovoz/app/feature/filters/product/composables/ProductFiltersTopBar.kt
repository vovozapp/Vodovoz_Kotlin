package com.vodovoz.app.feature.filters.product.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R

@Composable
fun ProductFiltersTopBar(
    modifier: Modifier = Modifier,
    showClearButton: Boolean,
    onClearClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
    ) {

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.CenterStart),
            visible = showClearButton,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = stringResource(R.string.reset),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onClearClick() }
                    .padding(6.dp)
            )
        }

        Text(
            modifier = Modifier
                .width(200.dp)
                .align(Alignment.Center),
            text = stringResource(R.string.filter),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis
        )

        Icon(
            painter = painterResource(id = R.drawable.icon_close),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(24.dp)
                .clip(CircleShape)
                .clickable {
                    onCloseClick()
                }
        )
    }
}