package com.vodovoz.app.design_system.composables.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.design_system.composables.card.LinearProductCard
import com.vodovoz.app.design_system.composables.decoration.SkeletonBox
import com.vodovoz.app.feature.home.model.ProductUi


@Suppress("NonSkippableComposable")
@Composable
fun ProductLazyList(
    products: List<ProductUi>,
    isGridView: Boolean,
    modifier: Modifier = Modifier,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isGridView) {
            items(
                products.size,
                key = { i -> products[i].id },
                span = { GridItemSpan(1) },
            ) { i ->
                GridProductCard(
                    product = products[i],
                    onClick = onProductClick,
                    onLike = onProductLike,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            items(
                products.size,
                key = { i -> products[i].id },
                span = { GridItemSpan(2) }
            ) { i ->
                LinearProductCard(
                    product = products[i],
                    onClick = onProductClick,
                    onLike = onProductLike,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ProductLazyPagingList(
    lazyPagingProducts: LazyPagingItems<ProductUi>,
    isGridView: Boolean,
    modifier: Modifier = Modifier,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
) {
    val loadState = lazyPagingProducts.loadState
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {


        if (loadState.refresh is LoadState.Loading || loadState.refresh is LoadState.Error) {
            items(8) {
                SkeletonBox(
                    shimmerState = shimmer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.85f)
                )
            }
        } else if (isGridView) {
            items(
                count = lazyPagingProducts.itemCount,
                span = { GridItemSpan(1) },
                contentType = lazyPagingProducts.itemContentType { "Products" },
            ) { i ->
                GridProductCard(
                    product = lazyPagingProducts[i] ?: return@items,
                    onClick = onProductClick,
                    onLike = onProductLike,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            items(
                count = lazyPagingProducts.itemCount,
                span = { GridItemSpan(2) },
                contentType = lazyPagingProducts.itemContentType { "Products" },
            ) { i ->
                LinearProductCard(
                    product = lazyPagingProducts[i] ?: return@items,
                    onClick = onProductClick,
                    onLike = onProductLike,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(2) }) {
                SkeletonBox(
                    shimmerState = shimmer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            }
        }

    }
}

fun LazyGridScope.gridProducts(
    lazyPagingProducts: LazyPagingItems<ProductUi>,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
) {
    val loadState = lazyPagingProducts.loadState

    if (loadState.refresh is LoadState.Loading || loadState.refresh is LoadState.Error) {
        items(8) {
            SkeletonBox(
                shimmerState = rememberShimmer(shimmerBounds = ShimmerBounds.View),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f)
            )
        }
    }

    items(
        count = lazyPagingProducts.itemCount,
        key = lazyPagingProducts.itemKey { it.id },
        span = { GridItemSpan(1) },
        contentType = lazyPagingProducts.itemContentType { "Products" },
    ) { i ->
        GridProductCard(
            product = lazyPagingProducts[i] ?: return@items,
            onClick = onProductClick,
            onLike = onProductLike,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (loadState.append is LoadState.Loading) {
        item(span = { GridItemSpan(2) }) {
            SkeletonBox(
                shimmerState = rememberShimmer(shimmerBounds = ShimmerBounds.View),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )
        }
    }
}
