package com.vodovoz.app.feature.favorite.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.chip.VodovozChip
import com.vodovoz.app.design_system.composables.list.ProductLazyList
import com.vodovoz.app.design_system.composables.list.ProductListHeader
import com.vodovoz.app.design_system.composables.tab_row.VodovozScrollableTabRow
import com.vodovoz.app.feature.home.model.PopularCategoryUi
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.product_comments.model.SortUi
import com.vodovoz.app.util.extensions.indexOfOrNull

@Suppress("NonSkippableComposable")
@Composable
fun FavoriteBody(
    modifier: Modifier = Modifier,
    products: List<ProductUi>,
    categories: List<PopularCategoryUi>,
    currentCategory: PopularCategoryUi,
    currentSort: SortUi,
    isGridView: Boolean,
    onCategoriesListClick: () -> Unit,
    onLayoutViewSwitch: () -> Unit,
    onSortingClick: () -> Unit,
) {
    Column(modifier = modifier) {

        VodovozScrollableTabRow(
            modifier = Modifier.padding(top = 8.dp),
            selectedTabIndex = categories.indexOfOrNull(currentCategory) ?: 0,
            edgePadding = 16.dp,
            spacing = 8.dp
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_category),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onCategoriesListClick() }
            )

            categories.forEach { category ->
                VodovozChip(
                    text = category.name,
                    selected = category == currentCategory,
                    onSelect = { }
                )
            }
        }

        ProductListHeader(
            modifier = Modifier.padding(top = 24.dp),
            sortName = currentSort.name,
            isGridView = isGridView,
            onSwitchClick = {
                onLayoutViewSwitch()
            },
            onSortingClick = {
                onSortingClick()
            }
        )


        ProductLazyList(
            products = products,
            isGridView = isGridView,
            onProductClick = {},
            onProductLike = {}
        )
    }
}