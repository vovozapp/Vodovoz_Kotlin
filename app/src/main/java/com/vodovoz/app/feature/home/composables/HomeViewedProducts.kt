package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.SectionUi
import com.vodovoz.app.domain.general.model.ButtonAction

@Composable
fun HomeViewedProducts(
    modifier: Modifier = Modifier,
    sectionViewedProducts: SectionUi<ProductUi>,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
    onShowAllClick: (ButtonAction) -> Unit,
) {
    Column(modifier = modifier) {
        TitleAndButton(
            title = sectionViewedProducts.title,
            button = sectionViewedProducts.button,
            onShowAllClick = { onShowAllClick(it) }
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .fillMaxWidth()
        ) {
            sectionViewedProducts.items.forEach { product ->
                GridProductCard(
                    modifier = Modifier.width(160.dp),
                    product = product,
                    onClick = onProductClick,
                    onLike = onProductLike
                )
            }
        }
    }
}