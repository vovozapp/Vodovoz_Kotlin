package com.vodovoz.app.feature.productlistnofilter.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.vodovoz.app.design_system.composables.list.ProductListCategoriesRow
import com.vodovoz.app.design_system.composables.list.ProductListOptionsRow
import com.vodovoz.app.design_system.composables.list.ProductListTitle
import com.vodovoz.app.design_system.composables.list.gridProducts
import com.vodovoz.app.design_system.composables.list.linearProducts
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.product_comments.model.SortUi

@Suppress("NonSkippableComposable")
@Composable
fun ProductsNoFilterBody(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState,
    title: String,
    categories: List<CategoryUi>,
    productsQuantity: String,
    currentCategory: CategoryUi,
    currentSort: SortUi,
    isGridView: Boolean,
    products: List<ProductUi>,
    productsLoadStates: CombinedLoadStates,
    onProductSee: (Int) -> Unit,
    onSortingClick: () -> Unit,
    onSwitchLayoutClick: () -> Unit,
    onCategoryClick: (CategoryUi) -> Unit,
    onCategoriesListClick: () -> Unit,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
    onFiltersClick: (() -> Unit)?,
) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            ProductListTitle(
                modifier = Modifier,
                productsQuantity = productsQuantity,
                title = title,
                onShareClick = { }
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            ProductListCategoriesRow(
                modifier = Modifier.padding(vertical = 16.dp),
                categories = categories,
                currentCategory = currentCategory,
                onCategoryClick = { category ->
                    onCategoryClick(category)
                },
                onCategoriesListClick = {
                    onCategoriesListClick()
                }
            )
        }

        stickyHeader {
            ProductListOptionsRow(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 8.dp),
                sortName = currentSort.name,
                isGridView = isGridView,
                onFiltersClick = onFiltersClick,
                onSortingClick = {
                    onSortingClick()
                },
                onSwitchClick = {
                    onSwitchLayoutClick()
                },
            )
        }


        if (isGridView) {
            gridProducts(
                products,
                productsLoadStates,
                shimmer,
                onProductSee,
                onProductClick,
                onProductLike
            )
        } else {
            linearProducts(
                products,
                productsLoadStates,
                shimmer,
                onProductSee,
                onProductClick,
                onProductLike
            )
        }
    }

//    Column(modifier = modifier) {
//        ProductListTitle(
//            modifier = Modifier.padding(top = 16.dp),
//            productsQuantity = productsQuantity,
//            title = title,
//            onShareClick = { }
//        )
//
//        if (categories.isNotEmpty()) {
//            ProductListCategoriesRow(
//                modifier = Modifier.padding(top = 16.dp),
//                categories = categories,
//                currentCategory = currentCategory,
//                onCategoryClick = { category ->
//                    onCategoryClick(category)
//                },
//                onCategoriesListClick = {
//                    onCategoriesListClick()
//                }
//            )
//        }
//
//        ProductListOptionsRow(
//            modifier = Modifier.padding(top = 24.dp),
//            sortName = currentSort.name,
//            isGridView = isGridView,
//            onSortingClick = {
//                onSortingClick()
//            },
//            onSwitchClick = {
//                onSwitchLayoutClick()
//            },
//            onFiltersClick = onFiltersClick
//        )
//
//
//        ProductLazyPagingList(
//            lazyGridState = lazyGridState,
//            products = products,
//            loadStates = productsLoadStates,
//            isGridView = isGridView,
//            onProductSee = { index ->
//                onProductSee(index)
//            },
//            onProductClick = { product ->
//                onProductClick(product)
//            },
//            onProductLike = { product ->
//                onProductLike(product)
//            }
//        )
//    }
}
