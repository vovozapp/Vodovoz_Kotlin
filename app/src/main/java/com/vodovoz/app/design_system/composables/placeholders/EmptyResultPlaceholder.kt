package com.vodovoz.app.design_system.composables.placeholders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.ClickableIcon


enum class EmptyResultPlaceholderItem {
    Arrow, Cross, None
}

@Composable
fun EmptyResultPlaceholder(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    imagePainter: Painter = painterResource(id = R.drawable.pic_search),
    item: EmptyResultPlaceholderItem = EmptyResultPlaceholderItem.None,
    onItemClick: () -> Unit = { },
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (item) {
                EmptyResultPlaceholderItem.Arrow -> {
                    ClickableIcon(
                        modifier = Modifier.clip(CircleShape),
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        tint = MaterialTheme.colorScheme.onBackground,
                        onClick = onItemClick
                    )
                }

                EmptyResultPlaceholderItem.Cross -> {
                    Spacer(modifier = Modifier.weight(1f))
                    ClickableIcon(
                        modifier = Modifier.clip(CircleShape),
                        painter = painterResource(id = R.drawable.icon_close),
                        tint = MaterialTheme.colorScheme.onBackground,
                        onClick = onItemClick
                    )
                }

                EmptyResultPlaceholderItem.None -> {

                }
            }
        }

        Spacer(modifier = Modifier.weight(0.6f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Image(
                painter = imagePainter,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = AnnotatedString.fromHtml(title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = AnnotatedString.fromHtml(description),
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center)
            )
        }
        Spacer(modifier = Modifier.weight(2.4f))
    }
}