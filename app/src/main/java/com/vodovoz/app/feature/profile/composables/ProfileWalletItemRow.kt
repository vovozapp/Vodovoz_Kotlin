package com.vodovoz.app.feature.profile.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vodovoz.app.feature.profile.model.ProfileWalletItemUi

@Suppress("NonSkippableComposable")
@Composable
fun ProfileWalletItemsRow(
    modifier: Modifier = Modifier,
    walletItems: List<ProfileWalletItemUi>,
    onCardClick: (ProfileWalletItemUi) -> Unit,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        walletItems.forEach { walletItem ->
            WalletItemCard(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                walletItem = walletItem,
                onClick = onCardClick
            )
        }
    }
}

@Composable
private fun WalletItemCard(
    modifier: Modifier = Modifier,
    walletItem: ProfileWalletItemUi,
    onClick: (ProfileWalletItemUi) -> Unit,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(walletItem.backgroundColor.takeOrElse { MaterialTheme.colorScheme.surface })
            .clickable { onClick(walletItem) },
    ) {
        AsyncImage(
            modifier = Modifier
                .align(Alignment.Bottom)
                .padding(start = 6.dp, end = 15.dp)
                .size(56.dp),
            model = ImageRequest.Builder(LocalContext.current).data(walletItem.imageUrl)
                .crossfade(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 6.dp, top = 4.dp, bottom = 4.dp)
        ) {
            Text(
                text = walletItem.title,
                color = walletItem.titleColor.takeOrElse { MaterialTheme.colorScheme.onBackground },
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = walletItem.description,
                color = walletItem.descriptionColor.takeOrElse { MaterialTheme.colorScheme.surfaceTint },
                style = MaterialTheme.typography.bodyMedium
            )
        }


    }
}