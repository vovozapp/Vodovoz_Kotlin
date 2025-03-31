package com.vodovoz.app.feature.productlistnofilter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.bottom_sheet.SortOptionsBottomSheet
import com.vodovoz.app.design_system.composables.list.ProductListTitle
import com.vodovoz.app.design_system.composables.placeholders.EmptyResultPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.composables.top_bar.VodovozSearchTopBar
import com.vodovoz.app.feature.productlistnofilter.composables.ProductsNoFilterBody


@OptIn(ExperimentalMaterial3Api::class)
@Suppress("NonSkippableComposable")
@Composable
fun ProductsNoFiltersScreen(
    viewModel: ProductsListNoFilterFlowViewModel,
    viewState: ProductsListNoFilterFlowViewModel.ProductListNoFilterState,
    lazyGridState: LazyGridState,
) {
    val productsSection = viewState.productsSection
    val pullRefreshState = rememberPullToRefreshState()



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val searchQuery =
            (viewModel.dataSource as? PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Search)?.query
                ?: ""

        VodovozSearchTopBar(
            value = searchQuery,
            onFocus = {
                viewModel.navigateToSearch(searchQuery)
            },
            onMicClick = {

            },
            onScanClick = {

            },
            onNavigationClick = {
                viewModel.navigateBack()
            }
        )


        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            state = pullRefreshState,
            isRefreshing = viewState.showRefreshIndicator,
            onRefresh = { viewModel.refresh() },
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
                ProductsListNoFilterFlowViewModel.UiState.Error -> {
                    NetworkErrorPlaceholder { viewModel.refresh() }
                }

                ProductsListNoFilterFlowViewModel.UiState.Loading -> {
                    LoadingPlaceholder()
                }

                ProductsListNoFilterFlowViewModel.UiState.Body -> {
                    ProductsNoFilterBody(
                        lazyGridState = lazyGridState,
                        title = productsSection.title,
                        productsQuantity = productsSection.productsQuantityText,
                        categories = productsSection.categories,
                        currentCategory = viewState.currentCategory,
                        currentSort = viewState.currentSort,
                        products = viewState.products,
                        productsLoadStates = viewState.productsLoadStates,
                        isGridView = viewState.isGridView,
                        onProductSee = { index ->
                            viewModel.notifyPagingProducts(index)
                        },
                        onSortingClick = {
                            viewModel.showSortBottomSheet()
                        },
                        onSwitchLayoutClick = {
                            viewModel.switchLayout()
                        },
                        onCategoryClick = { category ->
                            viewModel.selectCategory(category)
                        },
                        onCategoriesListClick = {
                            viewModel.navigateToCategories()
                        },
                        onProductClick = { product ->
                            viewModel.navigateToProductDetails(product)
                        },
                        onProductLike = { product ->
                            viewModel.changeFavorite(product)
                        },
                        onFiltersClick = if (viewModel.dataSource is PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Category) {
                            { viewModel.navigateToProductFilters() }
                        } else null
                    )
                }

                ProductsListNoFilterFlowViewModel.UiState.Empty -> {
                    EmptyResultPlaceholder(
                        title = stringResource(id = R.string.empty_products_title),
                        description = stringResource(id = R.string.empty_products_description)
                    )
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
