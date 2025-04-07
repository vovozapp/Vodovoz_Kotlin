package com.vodovoz.app.design_system.composables.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.valentinilk.shimmer.Shimmer
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.design_system.composables.card.LinearProductCard
import com.vodovoz.app.design_system.composables.decoration.SkeletonBox
import com.vodovoz.app.design_system.composables.placeholders.EmptyResultPlaceholder
import com.vodovoz.app.design_system.model.ProductUi


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

fun LazyGridScope.linearProducts(
    products: List<ProductUi>,
    loadState: CombinedLoadStates,
    shimmerState: Shimmer,
    onProductSee: (Int) -> Unit,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
) {

    when (loadState.refresh) {
        is LoadState.NotLoading -> {
            items(
                items = products,
                span = { GridItemSpan(2) },
            ) { product ->
                val currentIndex = products.indexOf(product)

                SideEffect {
                    onProductSee(currentIndex)
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    LinearProductCard(
                        product = product,
                        onClick = onProductClick,
                        onLike = onProductLike,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (currentIndex != products.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

            }

        }

        else -> {
            items(6, span = { GridItemSpan(2) }) { i ->
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SkeletonBox(
                        shimmerState = shimmerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                    if (i != products.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

    }



    item(span = { GridItemSpan(2) }) {
        if (loadState.append is LoadState.Loading) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                SkeletonBox(
                    shimmerState = shimmerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

}


fun LazyGridScope.gridProducts(
    products: List<ProductUi>,
    loadState: CombinedLoadStates,
    shimmerState: Shimmer,
    onProductSee: (Int) -> Unit,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
) {

    when (loadState.refresh) {
        is LoadState.NotLoading -> {
            items(products.size, span = { GridItemSpan(1) }) { index ->
                LaunchedEffect(index) {
                    onProductSee(index)
                }

                GridHorizontalPadding(isStartPadding = index % 2 == 0) {
                    GridProductCard(
                        product = products[index],
                        onClick = onProductClick,
                        onLike = onProductLike,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (index != products.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

        }

        else -> {
            items(8) { index ->
                GridHorizontalPadding(isStartPadding = index % 2 == 0) {
                    SkeletonBox(
                        shimmerState = shimmerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(255.dp)
                    )
                    if (index != products.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }


    }

    val countAppend = (products.size % 2) + 2
    items(countAppend, span = { GridItemSpan(1) }) { index ->
        if (loadState.append is LoadState.Loading) {
            GridHorizontalPadding(isStartPadding = if (countAppend % 2 == 0) index % 2 == 0 else index % 2 == 1) {
                SkeletonBox(
                    shimmerState = shimmerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(255.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

}

@Composable
inline fun GridHorizontalPadding(
    isStartPadding: Boolean,
    modifier: Modifier = Modifier,
    isEndPadding: Boolean = !isStartPadding,
    padding: Dp = 16.dp,
    content: ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.padding(
            start = if (isStartPadding) padding else 0.dp,
            end = if (isEndPadding) padding else 0.dp
        )
    ) {
        content()
    }
}


fun LazyGridScope.gridProducts(
    lazyPagingProducts: LazyPagingItems<ProductUi>,
    shimmerState: Shimmer,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
) {
    val loadState = lazyPagingProducts.loadState

    items(
        count = lazyPagingProducts.itemCount,
        key = lazyPagingProducts.itemKey { it.id },
        span = { GridItemSpan(1) },
        contentType = lazyPagingProducts.itemContentType { "Products" },
    ) { i ->
        Column {
            GridProductCard(
                product = lazyPagingProducts[i] ?: return@items,
                onClick = onProductClick,
                onLike = onProductLike,
                modifier = Modifier.fillMaxWidth()
            )
            if (i != lazyPagingProducts.itemCount - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (loadState.append is LoadState.Loading) {
        items((lazyPagingProducts.itemCount % 2) + 2, span = { GridItemSpan(1) }) {
            Column {
                SkeletonBox(
                    shimmerState = shimmerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(255.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


