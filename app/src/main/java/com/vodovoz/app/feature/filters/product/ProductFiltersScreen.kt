package com.vodovoz.app.feature.filters.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.floating.BottomFloatingContainer
import com.vodovoz.app.feature.filters.product.composables.ProductFiltersBody
import com.vodovoz.app.feature.filters.product.composables.ProductFiltersTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("NonSkippableComposable")
@Composable
fun ProductFiltersScreen(
    viewModel: ProductFiltersFlowViewModel,
    viewState: ProductFiltersFlowViewModel.ProductFiltersState,
    sliderState: RangeSliderState
) {
    val filters = viewState.filters
    Scaffold(
        topBar = {
            ProductFiltersTopBar(showClearButton = viewState.showClearButton, onClearClick = { viewModel.clearFilters() }) { viewModel.navigateBack() }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = viewState.showApplyButton,
                enter = slideInVertically { it },
                exit = slideOutVertically { -it }
            ) {
                BottomFloatingContainer {
                    VodovozButton(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(R.string.apply),
                        onClick = {
                            viewModel.navigateToProductList()
                        }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        ProductFiltersBody(
            modifier = Modifier.padding(paddingValues),
            sliderState = sliderState,
            filters = filters.filters,
            filterPrice = filters.price,
            onPriceChange = { range ->
                viewModel.changeFiltersPrice(range)
            },
            onFilterValueSelect = { filter, filterValue ->
                viewModel.selectFilterValue(filter, filterValue)
            },
            onShowAllFilterValuesClick = { filterUi ->
                viewModel.navigateToFilterValues(filterUi)
            },
            onFromChange = {
                viewModel.changePriceFromField(it)
            },
            onToChange = {
                viewModel.changePriceToField(it)
            }
        )
    }
}