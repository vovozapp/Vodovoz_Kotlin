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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.button.LayoutSwitchButton
import com.vodovoz.app.design_system.composables.button.SortingButton
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.design_system.composables.card.LinearProductCard
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

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        ) {
            SortingButton(
                text = viewState.currentSort.name,
                onClick = {
                    viewModel.showSortOptionsBottomSheet()
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            LayoutSwitchButton(
                isGridView = viewState.isGridView,
                onSwitch = { viewModel.switchLayoutView() })
        }

        val products = productsSection.products


        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (viewState.isGridView) {
                items(
                    items = products,
                    key = { it.id },
                    span = { GridItemSpan(1) }
                ) {
                    GridProductCard(
                        product = it,
                        onClick = { },
                        onLike = { },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                items(
                    items = products,
                    key = { it.id },
                    span = { GridItemSpan(2) }
                ) {
                    LinearProductCard(
                        product = it,
                        onClick = { },
                        onLike = { },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}