package com.vodovoz.app.feature.products_collection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.layout.LazyLayoutPinnableItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.button.LayoutSwitchButton
import com.vodovoz.app.design_system.composables.button.SortingButton
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.design_system.composables.card.LinearProductCard
import com.vodovoz.app.design_system.composables.list.ProductLazyList
import com.vodovoz.app.design_system.composables.list.ProductListHeader
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.products_collection.model.ProductsCollectionState

@Composable
fun ProductsCollectionScreen(
    viewModel: ProductsCollectionViewModel,
    viewState: ProductsCollectionState,
) {
    val productsSection = viewState.productsSection
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .consumeWindowInsets(WindowInsets.systemBars)
    ) {
        VodovozTopBar(
            onBack = {
                viewModel.navigateBack()
            },
            title = productsSection.title
        )

        ProductListHeader(
            modifier = Modifier.padding(top = 8.dp),
            sortName = viewState.currentSort.name,
            isGridView = viewState.isGridView,
            onSwitchClick = {
                viewModel.showSortOptionsBottomSheet()
            },
            onSortingClick = {
                viewModel.switchLayoutView()
            }
        )


        val products = productsSection.products

        ProductLazyList(
            products = products,
            isGridView = viewState.isGridView,
            onProductClick = {},
            onProductLike = {}
        )
    }
}