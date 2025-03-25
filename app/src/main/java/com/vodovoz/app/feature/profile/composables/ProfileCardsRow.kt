package com.vodovoz.app.feature.profile.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vodovoz.app.feature.profile.model.ProfileCardUi

@Suppress("NonSkippableComposable")
@Composable
fun ProfileCardsRow(
    modifier: Modifier = Modifier,
    cards: List<ProfileCardUi>,
    onCardClick: (ProfileCardUi) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        cards.take(3).forEach { card ->
            ProfileCard(
                card = card,
                modifier = Modifier
                    .weight(1f)
                    .height(76.dp),
                onClick = { profileCardUi ->
                    onCardClick(profileCardUi)
                }
            )
        }
    }
}

@Composable
private fun ProfileCard(
    modifier: Modifier = Modifier,
    card: ProfileCardUi,
    onClick: (ProfileCardUi) -> Unit,
) {
    val textStyle = with(MaterialTheme.typography.labelSmall) {
        copy(lineHeight = fontSize, letterSpacing = 0.sp)
    }

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            .clickable {
                onClick(card)
            }
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .crossfade(true)
                .crossfade(100)
                .data(card.imageUrl)
                .build(),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.FillBounds
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = card.title,
            color = card.titleColor.takeOrElse { MaterialTheme.colorScheme.onBackground },
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = card.description,
            color = card.descriptionColor.takeOrElse { MaterialTheme.colorScheme.onBackground },
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

    }
}