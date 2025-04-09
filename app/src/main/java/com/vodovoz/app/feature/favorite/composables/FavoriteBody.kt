package com.vodovoz.app.feature.favorite.composables

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
import com.vodovoz.app.design_system.composables.list.gridProducts
import com.vodovoz.app.design_system.composables.list.linearProducts
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.product_comments.model.SortUi

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
    onSwitchLayoutClick: () -> Unit,
    onSortingClick: () -> Unit,
    onCategoryClick: (CategoryUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
    onProductClick: (ProductUi) -> Unit,
    onProductAnalogsClick: (ProductUi) -> Unit,
    onIncrementProductToCart: (ProductUi) -> Unit,
    onDecrementProductToCart: (ProductUi) -> Unit,
) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            ProductListCategoriesRow(
                modifier = Modifier.padding(vertical = 8.dp),
                categories = categories,
                currentCategory = currentCategory,
                onCategoryClick = { category ->
                    onCategoryClick(category)
                },
                showEmptyCategory = true,
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
                products = products,
                loadState = productsLoadStates,
                shimmerState = shimmer,
                onProductSee = onProductSee,
                onProductClick = onProductClick,
                onProductLike = onProductLike,
                onProductAnalogsClick = onProductAnalogsClick,
                onIncrementProductToCart = onIncrementProductToCart,
                onDecrementProductToCart = onDecrementProductToCart
            )
        } else {
            linearProducts(
                products = products,
                loadState = productsLoadStates,
                shimmerState = shimmer,
                onProductSee = onProductSee,
                onProductClick = onProductClick,
                onProductLike = onProductLike,
                onProductAnalogsClick = onProductAnalogsClick,
                onIncrementProductToCart = onIncrementProductToCart,
                onDecrementProductToCart = onDecrementProductToCart
            )
        }
    }
}