package com.vodovoz.app.feature.filters.product

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.feature.filters.product.composables.ProductFiltersBody
import com.vodovoz.app.feature.filters.product.composables.ProductFiltersTopBar

@Suppress("NonSkippableComposable")
@Composable
fun ProductFiltersScreen(viewModel: ProductFiltersFlowViewModel, viewState: ProductFiltersFlowViewModel.ProductFiltersState) {
    val filters = viewState.filters
    Scaffold(
        topBar = {
            ProductFiltersTopBar { viewModel.navigateBack() }
        },
        contentWindowInsets = WindowInsets(0,0,0,0),
    ) { paddingValues ->
        ProductFiltersBody(
            modifier = Modifier.padding(paddingValues),
            filters = filters.filters,
            filterPrice = filters.price,
            onPriceChanged = { range ->
                viewModel.changeFiltersPrice(range)
            }
        )
    }
}