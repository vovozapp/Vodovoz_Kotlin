package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.feature.home.model.ProductUi

@Suppress("NonSkippableComposable")
@Composable
fun HomeNewProducts(
    modifier: Modifier = Modifier,
    newProducts: List<ProductUi>,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
    onShowAllClick: () -> Unit,
) {
    Column(modifier = modifier) {
        TitleAndButton(
            title = stringResource(R.string.new_products),
            onShowAllClick = { onShowAllClick() }
        )

        HomeRow(modifier = Modifier.padding(top = 16.dp)) { itemWidth ->
            newProducts.forEach { prduct ->
                GridProductCard(
                    modifier = Modifier.width(itemWidth),
                    product = prduct,
                    onClick = onProductClick,
                    onLike = onProductLike
                )
            }
        }
    }
}