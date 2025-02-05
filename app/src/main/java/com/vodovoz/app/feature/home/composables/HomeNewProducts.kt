package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.home.model.SectionUi

@Composable
fun HomeNewProducts(
    modifier: Modifier = Modifier,
    sectionNewProducts: SectionUi<ProductUi>,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
    onShowAllClick: () -> Unit,
) {
    Column(modifier = modifier) {
        TitleAndButton(
            title = sectionNewProducts.title,
            button = sectionNewProducts.button,
            onShowAllClick = { onShowAllClick() }
        )

        HomeRow(modifier = Modifier.padding(top = 16.dp)) { itemWidth ->
            sectionNewProducts.items.forEach { product ->
                GridProductCard(
                    modifier = Modifier.width(itemWidth),
                    product = product,
                    onClick = onProductClick,
                    onLike = onProductLike
                )
            }
        }
    }
}