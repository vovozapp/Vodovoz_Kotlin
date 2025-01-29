package com.vodovoz.app.design_system.composables.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.CommentUI

@Composable
fun CommentCard(modifier: Modifier = Modifier, commentUI: CommentUI) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.outlinedCardElevation(0.dp),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.background),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = commentUI.author ?: stringResource(id = R.string.anonymous),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                (1..5).forEach { starNumber ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = null,
                        tint = if (starNumber <= (commentUI.rating
                                ?: -1)
                        ) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            //todo parse text
            Text(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .heightIn(min = 40.dp),
                text = HtmlCompat.fromHtml(commentUI.text ?: "", HtmlCompat.FROM_HTML_MODE_COMPACT).toString(),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = commentUI.date ?: "",
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.surfaceTint,
                    style = MaterialTheme.typography.labelSmall
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_dislike),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(18.dp),
                    tint = MaterialTheme.colorScheme.surfaceTint
                )

                //todo dislikes
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "0",
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.surfaceTint,
                    style = MaterialTheme.typography.labelSmall
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_like_up),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(18.dp),
                    tint = MaterialTheme.colorScheme.surfaceTint

                )
                //todo likes
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "0",
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.surfaceTint,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}