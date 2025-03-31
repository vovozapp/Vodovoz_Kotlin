package com.vodovoz.app.feature.all.promotions.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vodovoz.app.design_system.composables.chip.TimeLeftChip
import com.vodovoz.app.design_system.composables.chip.VodovozColorChip
import com.vodovoz.app.design_system.composables.decoration.AdvertisingChip
import com.vodovoz.app.design_system.model.PromotionUi

@Composable
fun PromotionCard(
    modifier: Modifier = Modifier,
    promotion: PromotionUi,
    onClick: (PromotionUi) -> Unit,
    onAdvertisingClick: (PromotionUi) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable {
                onClick(promotion)
            }
    ) {
        AsyncImage(
            model = promotion.picture,
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        if (promotion.aboutAdvertisingUi != null) {
            AdvertisingChip(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = {
                    onAdvertisingClick(promotion)
                }
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val label = promotion.label

            label?.let {
                VodovozColorChip(color = label.color, text = label.name)
            }

            TimeLeftChip(text = promotion.timeLeft)
        }
    }
}