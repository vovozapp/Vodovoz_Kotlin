package com.vodovoz.app.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.feature.home.composables.HomeBody
import com.vodovoz.app.feature.home.composables.HomeLoadingPlaceholder
import com.vodovoz.app.feature.home.composables.HomeTopBar
import com.vodovoz.app.feature.home.composables.SpecialPromotionBottomSheet
import com.vodovoz.app.feature.home.composables.UnratedProductsBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("NonSkippableComposable")
@Composable
fun HomeScreen(
    viewState: HomeFlowViewModel.HomeState,
    viewModel: HomeFlowViewModel,
    topProductsLazyListState: LazyListState,
    onNavigateToQrCodeFragment: () -> Unit,
) {

    Scaffold(
        topBar = {
            HomeTopBar(
                onFocus = {
                    viewModel.navigateToSearch()
                },
                onMicClick = {

                },
                onScanClick = {
                    onNavigateToQrCodeFragment()
                              },
                onSearchClick = {
                    viewModel.navigateToSearch()
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
            when (viewState.uiState) {
                HomeFlowViewModel.HomeUiState.Loading -> {
                    HomeLoadingPlaceholder()
                }

                HomeFlowViewModel.HomeUiState.NetworkError -> {}
                HomeFlowViewModel.HomeUiState.Success -> {
                    HomeBody(
                        topProductsLazyListState = topProductsLazyListState,
                        banners = viewState.banners,
                        stories = viewState.stories,
                        sectionPromotions = viewState.sectionPromotions,
                        orderWithMenu = viewState.orderWithMenu,
                        sectionPopularCategories = viewState.sectionPopularCategories,
                        sectionNewProducts = viewState.sectionNewProducts,
                        sectionHurryUpBuyProducts = viewState.sectionHurryUpBuyProducts,
                        sectionTop = viewState.sectionTop,
                        sectionBottomProducts = viewState.sectionBottom,
                        sectionViewedProducts = viewState.sectionViewedProducts,
                        currentCategoryWithProducts = viewState.currentCategoryWithProducts,
                        onCategorySelect = { categoryWithProductsUi ->
                            viewModel.selectCategory(categoryWithProductsUi)
                        },
                        onMenuItemClick = {

                        },
                        onOrderClick = {

                        },
                        onPopularCategoryClick = { popularCategory ->
                            viewModel.navigateToPopularCategory(popularCategory)
                        },
                        onStoryClick = { story ->
                            viewModel.navigateToStories(story)
                        },
                        onPromotionClick = { promotion ->
                            viewModel.navigateToPromotionDetails(promotion)
                        },
                        onProductCardClick = { product ->
                            viewModel.navigateToProductDetails(product)
                        },
                        onProductLike = { product ->
                            viewModel.changeFavorite(product)
                        },
                        onShowAllClick = { action ->
                            viewModel.handleButtonAction(action)
                        }
                    )
                }
            }


            AnimatedVisibility(
                visible = viewState.showUnratedProducts,
                enter = slideInVertically(
                    tween(
                        durationMillis = 300,
                        easing = LinearEasing
                    )
                ) { it },
                exit = slideOutVertically(tween(durationMillis = 300, easing = LinearEasing)) { it }
            ) {
                UnratedProductsBottomSheet(
                    sectionUnratedProducts = viewState.sectionUnratedProducts,
                    onProductRatingChanged = { product, rating ->
                    },
                    onProductRatingChange = { product, rating ->
                        viewModel.changeUnratedProductRating(product, rating)
                    },
                    onDispose = {
                        viewModel.closeUnratedProductsBottomSheet()
                    }
                )
            }
        }

    }

    if (viewState.showSpecialPromotion) {
        SpecialPromotionBottomSheet(
            specialPromotionUi = viewState.specialPromotion,
            onDismissRequest = { viewModel.closeSpecialPromotionBottomSheet() },
            onButtonClick = { }
        )
    }

}