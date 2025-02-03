package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.design_system.composables.chip.VodovozChip
import com.vodovoz.app.design_system.composables.tab_row.VodovozTabRow
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.home.model.CategoryWithProductsUi
import com.vodovoz.app.util.extensions.indexOfOrNull

@Suppress("NonSkippableComposable")
@Composable
fun HomeBestOffers(
    modifier: Modifier = Modifier,
    onShowAllClick: () -> Unit,
    currentCategoryWithProducts: CategoryWithProductsUi,
    categoryWithProductsList: List<CategoryWithProductsUi>,
    onCategorySelect: (CategoryWithProductsUi) -> Unit,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
) {
    Column(modifier = modifier) {
        HomeTitleAndAll(
            title = stringResource(R.string.best_offers),
            onShowAllClick = onShowAllClick
        )


        VodovozTabRow(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            selectedTabIndex = categoryWithProductsList.indexOfOrNull(currentCategoryWithProducts) ?: 0,
            edgePadding = 16.dp,
            spacing = 8.dp
        ) {
            categoryWithProductsList.forEach { sectionWithProducts ->
                VodovozChip(
                    text = sectionWithProducts.name,
                    selected = currentCategoryWithProducts == sectionWithProducts,
                    onSelect = { onCategorySelect(sectionWithProducts) }
                )
            }
        }

        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(currentCategoryWithProducts.products) { product ->
                GridProductCard(
                    modifier = Modifier.fillParentMaxWidth(0.49f),
                    product = product,
                    onClick = onProductClick,
                    onLike = onProductLike
                )
            }
        }


    }
}