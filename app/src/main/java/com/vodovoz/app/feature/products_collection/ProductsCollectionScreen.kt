package com.vodovoz.app.feature.products_collection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.list.ProductLazyList
import com.vodovoz.app.design_system.composables.list.ProductListOptionsRow
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.products_collection.composables.ProductsCollectionPlaceholder
import com.vodovoz.app.feature.products_collection.model.ProductsCollectionState
import com.vodovoz.app.feature.products_collection.model.ProductsCollectionUiState

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

        when(viewState.uiState){
            ProductsCollectionUiState.Loading -> {
                ProductsCollectionPlaceholder()
            }
            ProductsCollectionUiState.Success -> {
                ProductListOptionsRow(
                    modifier = Modifier.padding(top = 8.dp),
                    sortName = viewState.currentSort.name,
                    isGridView = viewState.isGridView,
                    onSwitchClick = {
                        viewModel.switchLayout()
                    },
                    onSortingClick = {
                        viewModel.showSortOptionsBottomSheet()
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

    }
}