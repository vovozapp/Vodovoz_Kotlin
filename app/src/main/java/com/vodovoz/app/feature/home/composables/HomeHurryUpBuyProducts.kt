package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.domain.general.model.ButtonAction
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.SectionUi

@Composable
fun HomeHurryUpBuyProducts(
    modifier: Modifier = Modifier,
    sectionHurryUpBuyProducts: SectionUi<ProductUi>,
    onShowAllClick: (ButtonAction) -> Unit,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
) {
    Column(modifier = modifier) {
        TitleAndButton(
            title = sectionHurryUpBuyProducts.title,
            button = sectionHurryUpBuyProducts.button,
            onShowAllClick = { onShowAllClick(it) }
        )

        HomeRow(
            modifier = Modifier.padding(top = 16.dp)
        ) { itemWidth ->
            sectionHurryUpBuyProducts.items.forEach { product ->
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