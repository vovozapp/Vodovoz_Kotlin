package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.design_system.composables.chip.VodovozChip
import com.vodovoz.app.design_system.composables.tab_row.VodovozScrollableTabRow
import com.vodovoz.app.domain.general.model.ButtonAction
import com.vodovoz.app.design_system.model.CategoryWithProductsUi
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.SectionUi
import com.vodovoz.app.util.extensions.indexOfOrNull

@Composable
fun HomeTopProducts(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    currentCategoryWithProducts: CategoryWithProductsUi,
    sectionCategoriesWithProducts: SectionUi<CategoryWithProductsUi>,
    onShowAllClick: (ButtonAction) -> Unit,
    onCategorySelect: (CategoryWithProductsUi) -> Unit,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
) {

    val button = sectionCategoriesWithProducts.button
    Column(modifier = modifier) {
        TitleAndButton(
            title = sectionCategoriesWithProducts.title,
            button = button,
            onShowAllClick = { onShowAllClick(it) }
        )

        VodovozScrollableTabRow(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            selectedTabIndex = sectionCategoriesWithProducts.items.indexOfOrNull(
                currentCategoryWithProducts
            ) ?: 0,
            edgePadding = 16.dp,
            spacing = 8.dp
        ) {
            sectionCategoriesWithProducts.items.forEach { sectionWithProducts ->
                VodovozChip(
                    text = sectionWithProducts.name,
                    selected = currentCategoryWithProducts == sectionWithProducts,
                    onSelect = { onCategorySelect(sectionWithProducts) }
                )
            }
        }


        LazyRow(
            state = lazyListState,
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