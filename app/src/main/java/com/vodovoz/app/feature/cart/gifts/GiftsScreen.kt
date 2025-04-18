package com.vodovoz.app.feature.cart.gifts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.button.VodovozRadioButton
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.cart.gifts.model.GiftsState
import com.vodovoz.app.feature.cart.model.CartPresentItemUi

@Suppress("NonSkippableComposable")
@Composable
fun GiftsScreen(viewModel: GiftsViewModel, viewState: GiftsState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        VodovozTopBar(
            onBack = {
                viewModel.navigateBack()
            },
            title = stringResource(id = R.string.choose_present)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp)
        ) {
            viewState.gifts.forEachIndexed { index, gift ->
                Column {
                    key(gift.name + gift.id) {
                        GiftItem(
                            item = gift,
                            selected = viewState.currentGift == gift
                        ) { presentItem ->
                            viewModel.selectGift(presentItem)
                        }
                    }
                    if (viewState.gifts.lastIndex != index) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                }
            }
        }

        val button = viewState.button
        VodovozButton(
            modifier = Modifier.padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
            text = button.name,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = button.backgroundColor.takeOrElse { MaterialTheme.colorScheme.primary },
                contentColor = button.textColor.takeOrElse { MaterialTheme.colorScheme.background }
            ),
            onClick = {
                viewModel.chooseGift()
            }
        )
    }
}

@Composable
private fun GiftItem(
    modifier: Modifier = Modifier,
    item: CartPresentItemUi,
    selected: Boolean,
    onClick: (CartPresentItemUi) -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onClick(item) },
                interactionSource = null,
                indication = null
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context).data(item.image).crossfade(true).build(),
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.zero_with_rub),
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }

        VodovozRadioButton(selected = selected, onClick = {})

    }
}