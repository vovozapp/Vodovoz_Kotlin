package com.vodovoz.app.feature.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import coil3.compose.rememberAsyncImagePainter
import com.vodovoz.app.R
import com.vodovoz.app.feature.search.composables.SearchEmptyPlaceholder
import com.vodovoz.app.feature.search.composables.SearchLoadingPlaceholder
import com.vodovoz.app.feature.search.composables.SearchScreenBody
import com.vodovoz.app.feature.search.composables.SearchTopBar

@Suppress("NonSkippableComposable")
@Composable
fun SearchScreen(viewModel: SearchFlowViewModel, viewState: SearchFlowViewModel.SearchState) {
    Scaffold(
        topBar = {
            SearchTopBar(
                value = viewState.query,
                onValueChange = { query ->
                    viewModel.changeQuery(query)
                },
                onClearClick = {
                    viewModel.changeQuery("")
                },
                onScanClick = {
                    viewModel.navigateToScan()
                },
                onSearchClick = {
                    viewModel.search()
                },
                onNavigationClick = {
                    viewModel.navigateBack()
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            when (val uiState = viewState.uiState) {
                is SearchFlowViewModel.UiState.Empty -> {
                    SearchEmptyPlaceholder(
                        imagePainter = if (uiState.image.isEmpty()) {
                            painterResource(id = R.drawable.pic_search)
                        } else {
                            rememberAsyncImagePainter(
                                model = uiState.image
                            )
                        },
                        description = uiState.description
                    )
                }

                SearchFlowViewModel.UiState.Error -> {
                }

                SearchFlowViewModel.UiState.Loading -> {
                    SearchLoadingPlaceholder()
                }

                SearchFlowViewModel.UiState.Success -> {
                    SearchScreenBody(
                        matchingQueries = viewState.matchingQueries,
                        sectionRecommendations = viewState.sectionRecommendations,
                        searchHistory = viewState.searchHistory,
                        onQueryChoose = { query ->
                            viewModel.chooseMatchingQuery(query)
                        },
                        onQueryClose = { searchQuery ->
                            viewModel.removeSearchQuery(searchQuery)
                        },
                        onProductLikeClick = { product ->
                            viewModel.changeFavorite(product)
                        },
                        onProductCardClick = { product ->
                            viewModel.navigateToProductDetails(product)
                        }
                    )
                }
            }
        }
    }
}