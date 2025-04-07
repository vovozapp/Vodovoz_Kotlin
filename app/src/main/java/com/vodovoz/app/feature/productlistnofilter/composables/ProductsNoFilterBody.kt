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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import coil3.compose.rememberAsyncImagePainter
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.list.ProductListCategoriesRow
import com.vodovoz.app.design_system.composables.list.ProductListOptionsRow
import com.vodovoz.app.design_system.composables.list.ProductListTitle
import com.vodovoz.app.design_system.composables.list.gridProducts
import com.vodovoz.app.design_system.composables.list.linearProducts
import com.vodovoz.app.design_system.composables.placeholders.EmptyResultPlaceholder
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.domain.general.model.EmptyResultException
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
    showCategoryList: Boolean,
    showFilters: Boolean,
    showEmptyCategory: Boolean,
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
    onShareClick: () -> Unit,
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
                modifier = Modifier.padding(bottom = 16.dp),
                productsQuantity = productsQuantity,
                title = title,
                onShareClick = onShareClick
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            if (categories.isNotEmpty() || showCategoryList) {
                ProductListCategoriesRow(
                    modifier = Modifier.padding(bottom = 16.dp),
                    categories = categories,
                    showEmptyCategory = showEmptyCategory,
                    currentCategory = currentCategory,
                    onCategoryClick = { category ->
                        onCategoryClick(category)
                    },
                    onCategoriesListClick = if (showCategoryList) onCategoriesListClick else null
                )
            }

        }

        stickyHeader {
            ProductListOptionsRow(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 8.dp),
                sortName = currentSort.name,
                isGridView = isGridView,
                onFiltersClick = if (showFilters) onFiltersClick else null,
                onSortingClick = {
                    onSortingClick()
                },
                onSwitchClick = {
                    onSwitchLayoutClick()
                },
            )
        }


        val refreshLoadState = productsLoadStates.refresh
        if (refreshLoadState is LoadState.Error && refreshLoadState.error is EmptyResultException) {
            item(span = { GridItemSpan(2) }) {
                //todo - do map domain state
                val errorData =
                    (refreshLoadState.error as? EmptyResultException)?.errorData ?: return@item

                EmptyResultPlaceholder(
                    title = errorData.headerHtml,
                    description = errorData.descriptionHtml,
                    imagePainter = if (errorData.imageUrl.isNotBlank()) {
                        rememberAsyncImagePainter(errorData.imageUrl)
                    } else painterResource(id = R.drawable.pic_search)
                )
            }
        } else if (isGridView) {
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
