package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.ui.model.ProductUI

@Suppress("NonSkippableComposable")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailsBuyWithProduct(
    modifier: Modifier = Modifier,
    buyWithProductUIList: List<ProductUI>,
    onProductLike: (ProductUI) -> Unit,
    onProductClick: (ProductUI) -> Unit,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.you_viewed),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )

        FlowRow(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2,

            ) {
            buyWithProductUIList.forEach { product ->
                GridProductCard(
                    modifier = Modifier.weight(1f),
                    product = product,
                    onClick = onProductClick,
                    onLike = onProductLike
                )
            }
            if (buyWithProductUIList.size % 2 == 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

}