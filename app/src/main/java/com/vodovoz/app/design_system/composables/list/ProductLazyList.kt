package com.vodovoz.app.design_system.composables.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.design_system.composables.card.LinearProductCard
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
                span = { GridItemSpan(1) }
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
