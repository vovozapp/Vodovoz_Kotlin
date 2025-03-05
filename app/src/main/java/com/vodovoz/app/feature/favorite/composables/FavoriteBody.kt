package com.vodovoz.app.feature.favorite.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.vodovoz.app.design_system.composables.list.ProductLazyPagingList
import com.vodovoz.app.design_system.composables.list.ProductListCategoriesRow
import com.vodovoz.app.design_system.composables.list.ProductListOptionsRow
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.product_comments.model.SortUi

@Suppress("NonSkippableComposable")
@Composable
fun FavoriteBody(
    modifier: Modifier = Modifier,
    lazyPagingProducts: LazyPagingItems<ProductUi>,
    categories: List<CategoryUi>,
    currentCategory: CategoryUi,
    currentSort: SortUi,
    isGridView: Boolean,
    onCategoriesListClick: () -> Unit,
    onLayoutViewSwitch: () -> Unit,
    onSortingClick: () -> Unit,
    onCategoryClick: (CategoryUi) -> Unit
) {
    Column(modifier = modifier) {

        ProductListCategoriesRow(
            modifier = Modifier.padding(top = 8.dp),
            categories = categories,
            currentCategory = currentCategory,
            onCategoryClick = { categoryUi -> onCategoryClick(categoryUi) },
            onCategoriesListClick = { onCategoriesListClick() })



        ProductListOptionsRow(
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


        ProductLazyPagingList(
            lazyPagingProducts = lazyPagingProducts,
            isGridView = isGridView,
            onProductClick = {

            },
            onProductLike = {

            }
        )
    }
}