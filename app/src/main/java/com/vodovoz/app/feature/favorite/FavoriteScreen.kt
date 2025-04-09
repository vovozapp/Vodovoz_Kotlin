package com.vodovoz.app.feature.favorite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    lazyGridState: LazyGridState,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        FavoriteTopBar(
            onSearchClick = {
                viewModel.navigateToSearch()
            },
            showSearch = viewState.uiState is FavoriteFlowViewModel.FavoriteUiState.Success || viewState.uiState is FavoriteFlowViewModel.FavoriteUiState.Loading
        )

        val categories = viewState.productsSection.categories


        val pullRefreshState = rememberPullToRefreshState()

        PullToRefreshBox(
            state = pullRefreshState,
            isRefreshing = viewState.showRefreshIndicator,
            onRefresh = {
                viewModel.refresh()
            },
            indicator = {
                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    state = pullRefreshState,
                    containerColor = MaterialTheme.colorScheme.background,
                    color = MaterialTheme.colorScheme.primary,
                    isRefreshing = viewState.showRefreshIndicator
                )
            }
        ) {

            when (viewState.uiState) {
                FavoriteFlowViewModel.FavoriteUiState.Empty -> {
                    FavoriteEmpty(onButtonClick = { viewModel.navigateToCatalog() })
                }

                FavoriteFlowViewModel.FavoriteUiState.Loading -> {
                    LoadingPlaceholder()
                }

                FavoriteFlowViewModel.FavoriteUiState.Success -> {
                    FavoriteBody(
                        categories = categories,
                        currentCategory = viewState.currentCategory,
                        currentSort = viewState.currentSort,
                        isGridView = viewState.isGridView,
                        products = viewState.products,
                        lazyGridState = lazyGridState,
                        productsLoadStates = viewState.productsLoadStates,
                        onCategoriesListClick = {
                            viewModel.navigateToCategories()
                        },
                        onSwitchLayoutClick = {
                            viewModel.switchLayout()
                        },
                        onSortingClick = {
                            viewModel.showSortBottomSheet()
                        },
                        onCategoryClick = { categoryUi ->
                            viewModel.selectCategory(categoryUi)
                        },
                        onProductLike = { product ->
                            viewModel.changeFavorite(product)
                        },
                        onProductClick = { product ->
                            viewModel.navigateToProductDetails(product)
                        },
                        onProductSee = { index ->
                            viewModel.notifyPagingProducts(index)
                        },
                        onProductAnalogsClick = { product ->
                            viewModel.navigateToProductAnalogs(product)
                        },
                        onIncrementProductToCart = { product ->
                            viewModel.incrementProductToCart(product)
                        },
                        onDecrementProductToCart = { product ->
                            viewModel.decrementProductToCart(product)
                        }
                    )
                }

                FavoriteFlowViewModel.FavoriteUiState.Error -> {

                }
            }
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