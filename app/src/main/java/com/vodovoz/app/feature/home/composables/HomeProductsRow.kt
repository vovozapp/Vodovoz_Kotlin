package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.SectionUi
import com.vodovoz.app.domain.general.model.ButtonAction


@Composable
fun HomeProductsRow(
    modifier: Modifier = Modifier,
    sectionProducts: SectionUi<ProductUi>,
    onShowAllClick: (ButtonAction) -> Unit,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
    onIncrementToCart: (ProductUi) -> Unit,
    onDecrementToCart: (ProductUi) -> Unit,
    onAnalogsClick: (ProductUi) -> Unit
) {
    if (sectionProducts.items.isNotEmpty()) {
        Column(modifier = modifier) {
            TitleAndButton(
                title = sectionProducts.title,
                button = sectionProducts.button,
                onShowAllClick = { onShowAllClick(it) }
            )

            LazyRow(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sectionProducts.items) { item ->
                    GridProductCard(
                        modifier = Modifier.width(160.dp),
                        product = item,
                        onClick = onProductClick,
                        onLike = onProductLike,
                        onIncrementToCart = onIncrementToCart,
                        onDecrementToCart = onDecrementToCart,
                        onAnalogsClick = onAnalogsClick
                    )
                }
            }
        }
    }
}