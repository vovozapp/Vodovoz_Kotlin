package com.vodovoz.app.feature.favorite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.vodovoz.app.design_system.composables.bottom_sheet.SortOptionsBottomSheet
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.feature.favorite.composables.FavoriteBody
import com.vodovoz.app.feature.favorite.composables.FavoriteEmpty
import com.vodovoz.app.feature.favorite.composables.FavoriteTopBar

@OptIn(ExperimentalMaterial3Api::class)
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

        val categories = viewState.productsSection.categories
        val lazyPagingProducts = viewState.pagedProducts.collectAsLazyPagingItems()

        when (viewState.uiState) {
            FavoriteFlowViewModel.FavoriteUiState.Empty -> {
                FavoriteEmpty()
            }

            FavoriteFlowViewModel.FavoriteUiState.Loading -> {
                LoadingPlaceholder()
            }

            FavoriteFlowViewModel.FavoriteUiState.Success -> {
                FavoriteBody(
                    lazyPagingProducts = lazyPagingProducts,
                    categories = categories,
                    currentCategory = viewState.currentCategory,
                    currentSort = viewState.currentSort,
                    isGridView = viewState.isGridView,
                    onCategoriesListClick = {
                        viewModel.navigateToCategories()
                    },
                    onLayoutViewSwitch = {
                        viewModel.switchLayout()
                    },
                    onSortingClick = {
                        viewModel.showSortBottomSheet()
                    }
                )
            }

            else -> {}
        }
    }

    if (viewState.showSortBottomSheet) {
        SortOptionsBottomSheet(
            onDismissRequest = { viewModel.hideSortBottomSheet() },
            currentSort = viewState.currentSort,
            sorting = viewState.productsSection.sorting,
            onSortSelect = { sort -> viewModel.selectSort(sort) }
        )
    }
}