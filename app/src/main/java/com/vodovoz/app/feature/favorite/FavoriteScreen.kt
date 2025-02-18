package com.vodovoz.app.feature.favorite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.feature.favorite.composables.FavoriteBody
import com.vodovoz.app.feature.favorite.composables.FavoriteEmpty
import com.vodovoz.app.feature.favorite.composables.FavoriteTopBar

@Suppress("NonSkippableComposable")
@Composable
fun FavoriteScreen(
    viewModel: FavoriteFlowViewModel,
    viewState: FavoriteFlowViewModel.FavoriteState,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.systemBars)
            .consumeWindowInsets(WindowInsets.systemBars)
    ) {
        FavoriteTopBar(
            onSearchClick = {

            },
            showSearch = viewState.uiState is FavoriteFlowViewModel.FavoriteUiState.Success || viewState.uiState is FavoriteFlowViewModel.FavoriteUiState.Loading
        )

        val categories = viewState.categories

        when (viewState.uiState) {
            FavoriteFlowViewModel.FavoriteUiState.Empty -> {
                FavoriteEmpty()
            }

            FavoriteFlowViewModel.FavoriteUiState.Loading -> {

            }

            FavoriteFlowViewModel.FavoriteUiState.Success -> {
                FavoriteBody(
                    products = viewState.productsSection.products,
                    categories = categories,
                    currentCategory = viewState.currentCategory,
                    currentSort = viewState.currentSort,
                    isGridView = viewState.isGridView,
                    onCategoriesListClick = {
                        viewModel.navigateToCategories()
                    },
                    onLayoutViewSwitch = {
                        viewModel.switchLayoutView()
                    },
                    onSortingClick = {
                        viewModel.showSortOptionsBottomSheet()
                    }
                )
            }

            else -> {}
        }
    }
}