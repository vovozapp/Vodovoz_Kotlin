package com.vodovoz.app.feature.favorite.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.vodovoz.app.design_system.composables.list.ProductListCategoriesRow
import com.vodovoz.app.design_system.composables.list.ProductListOptionsRow
import com.vodovoz.app.design_system.composables.list.gridProducts
import com.vodovoz.app.design_system.composables.list.linearProducts
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.product_comments.model.SortUi
import com.vodovoz.app.util.extensions.debugLog

@Suppress("NonSkippableComposable")
@Composable
fun FavoriteBody(
    modifier: Modifier = Modifier,
    onProductSee: (Int) -> Unit,
    categories: List<CategoryUi>,
    products: List<ProductUi>,
    productsLoadStates: CombinedLoadStates,
    currentCategory: CategoryUi,
    currentSort: SortUi,
    isGridView: Boolean,
    lazyGridState: LazyGridState,
    onCategoriesListClick: () -> Unit,
    onLayoutViewSwitch: () -> Unit,
    onSortingClick: () -> Unit,
    onCategoryClick: (CategoryUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
    onProductClick: (ProductUi) -> Unit,
) {
    Column(modifier = modifier) {

        ProductListCategoriesRow(
            modifier = Modifier.padding(top = 8.dp),
            categories = categories,
            currentCategory = currentCategory,
            onCategoryClick = { categoryUi -> onCategoryClick(categoryUi) },
            onCategoriesListClick = { onCategoriesListClick() }
        )



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

        val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize()
                .nestedScroll(object : NestedScrollConnection {

                    override fun onPostScroll(
                        consumed: Offset,
                        available: Offset,
                        source: NestedScrollSource,
                    ): Offset {
                        debugLog { "onPostScroll offset: $consumed" }
                        return super.onPostScroll(consumed, available, source)
                    }
                }),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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


//        ProductLazyPagingList(
//            lazyPagingProducts = lazyPagingProducts,
//            isGridView = isGridView,
//            onProductClick = { productUi ->
//                onProductClick(productUi)
//            },
//            onProductLike = { productUi ->
//                onProductLike(productUi)
//            }
//        )
    }
}