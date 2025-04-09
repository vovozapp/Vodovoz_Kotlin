package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.domain.general.model.ButtonAction
import com.vodovoz.app.design_system.model.CategoryWithProductsUi
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.SectionUi

@Composable
fun HomeBottomProducts(
    modifier: Modifier = Modifier,
    sectionBottomProducts: SectionUi<CategoryWithProductsUi>,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
    onShowAllClick: (ButtonAction) -> Unit,
    onIncrementToCart: (ProductUi) -> Unit,
    onDecrementToCart: (ProductUi) -> Unit,
    onProductAnalogsClick: (ProductUi) -> Unit
) {
    Column(modifier = modifier) {
        TitleAndButton(
            title = sectionBottomProducts.title,
            button = sectionBottomProducts.button,
            onShowAllClick = { onShowAllClick(it) }
        )

        val categoryWithProducts = sectionBottomProducts.items.firstOrNull() ?: return@Column
        val products = categoryWithProducts.products



        LazyRow(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { item ->
                GridProductCard(
                    modifier = Modifier.width(160.dp),
                    product = item,
                    onClick = onProductClick,
                    onLike = onProductLike,
                    onIncrementToCart = onIncrementToCart,
                    onDecrementToCart = onDecrementToCart,
                    onAnalogsClick = onProductAnalogsClick
                )
            }
        }
    }
}