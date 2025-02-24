package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.domain.general.model.ButtonAction
import com.vodovoz.app.feature.home.model.CategoryWithProductsUi
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.home.model.SectionUi

@Composable
fun HomeBottomProducts(
    modifier: Modifier = Modifier,
    sectionBottomProducts: SectionUi<CategoryWithProductsUi>,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
    onShowAllClick: (ButtonAction) -> Unit,
) {
    Column(modifier = modifier) {
        TitleAndButton(
            title = sectionBottomProducts.title,
            button = sectionBottomProducts.button,
            onShowAllClick = { onShowAllClick(it) }
        )

        val categoryWithProducts = sectionBottomProducts.items.firstOrNull()
        val products = categoryWithProducts?.products ?: emptyList()

        HomeRow(modifier = Modifier.padding(top = 16.dp)) { itemWidth ->
            products.forEach { product ->
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