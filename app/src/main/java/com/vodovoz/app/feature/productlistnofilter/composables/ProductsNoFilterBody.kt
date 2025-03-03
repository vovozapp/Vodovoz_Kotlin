package com.vodovoz.app.feature.productlistnofilter.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.vodovoz.app.design_system.composables.list.ProductLazyPagingList
import com.vodovoz.app.design_system.composables.list.ProductListCategoriesRow
import com.vodovoz.app.design_system.composables.list.ProductListOptionsRow
import com.vodovoz.app.design_system.composables.list.ProductListTitle
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.product_comments.model.SortUi

@Suppress("NonSkippableComposable")
@Composable
fun ProductsNoFilterBody(
    modifier: Modifier = Modifier,
    title: String,
    categories: List<CategoryUi>,
    productsQuantity: String,
    currentCategory: CategoryUi,
    currentSort: SortUi,
    lazyPagingProducts: LazyPagingItems<ProductUi>,
    isGridView: Boolean,
    onSortingClick: () -> Unit,
    onSwitchLayoutClick: () -> Unit,
    onCategoryClick: (CategoryUi) -> Unit,
    onCategoriesListClick: () -> Unit
) {
    Column(modifier = modifier) {
        ProductListTitle(
            modifier = Modifier.padding(top = 16.dp),
            productsQuantity = productsQuantity,
            title = title,
            onShareClick = { }
        )

        ProductListCategoriesRow(
            modifier = Modifier.padding(top = 16.dp),
            categories = categories,
            currentCategory = currentCategory,
            onCategoryClick = { category ->
                onCategoryClick(category)
            },
            onCategoriesListClick = {
                onCategoriesListClick()
            }
        )

        ProductListOptionsRow(
            modifier = Modifier.padding(top = 24.dp),
            sortName = currentSort.name,
            isGridView = isGridView,
            onSortingClick = {
                onSortingClick()
            },
            onSwitchClick = {
                onSwitchLayoutClick()
            }
        )


        ProductLazyPagingList(
            lazyPagingProducts = lazyPagingProducts,
            isGridView = isGridView,
            onProductClick = { },
            onProductLike = { }
        )
    }
}