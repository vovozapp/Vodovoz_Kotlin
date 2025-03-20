package com.vodovoz.app.feature.filters.concrete

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.floating.BottomFloatingContainer
import com.vodovoz.app.feature.filters.concrete.adapter.composable.FilterValuesBody
import com.vodovoz.app.feature.filters.concrete.adapter.composable.FilterValuesTopBar

@Suppress("NonSkippableComposable")
@Composable
fun FilterValuesScreen(
    viewModel: ConcreteFilterFlowViewModel,
    viewState: ConcreteFilterFlowViewModel.ConcreteFilterState,
) {
    Scaffold(
        topBar = {
            FilterValuesTopBar(
                title = viewState.filter.name,
                searchQuery = viewState.searchQuery,
                isSearchMode = viewState.isSearchMode,
                onNavigationClick = {
                    viewModel.navigateBack()
                },
                onSearchQueryChange = { s ->
                    viewModel.changeSearchQuery(s)
                },
                onSearchModeChange = { isSearchMode ->
                    viewModel.changeSearchMode(isSearchMode)
                }
            )
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
                            viewModel.navigateToProductFilters()
                        }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        FilterValuesBody(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            searchQuery = viewState.searchQuery,
            filterValues = viewState.filter.values,
            onFilterValueSelect = { filterValue ->
                viewModel.selectFilterValue(filterValue)
            }
        )
    }
}