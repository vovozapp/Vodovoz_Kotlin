package com.vodovoz.app.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vodovoz.app.feature.all.promotions.composables.AdvertisingInfoBottomSheet
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
    pullRefreshState: PullToRefreshState,
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
                    viewModel.showSpeechRecognizer()
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

        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            isRefreshing = viewState.showRefreshIndicator,
            onRefresh = {
                viewModel.refresh()
            },
            state = pullRefreshState,
            indicator = {
                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = viewState.showRefreshIndicator,
                    state = pullRefreshState,
                    containerColor = MaterialTheme.colorScheme.background,
                    color = MaterialTheme.colorScheme.primary
                )

            }

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
                        onMenuItemClick = { menuItem ->
                            viewModel.navigateByMenuItem(menuItem)
                        },
                        onOrderClick = {
                            viewModel.navigateToOrderDetails(it)
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
                        },
                        onAboutAdvertisingClick = { aboutAdvertisingUi ->
                            viewModel.showAdvertisingBottomSheet(aboutAdvertisingUi)
                        },
                        onBannerClick = { banner ->
                            viewModel.activateBannerAction(banner)
                        },
                        onIncrementProductToCart = { product ->
                            viewModel.incrementProductToCart(product)
                        },
                        onDecrementProductToCart = { product ->
                            viewModel.decrementProductToCart(product)
                        },
                        onProductAnalogsClick = { product ->
                            viewModel.navigateToProductAnalogs(product)
                        }
                    )
                }
            }

        }
    }

    if (viewState.showAdvertisingBS) {
        AdvertisingInfoBottomSheet(
            advertising = viewState.currentAdvertising,
            onDismissRequest = { viewModel.closeAdvertisingBottomSheet() }
        )
    }


    if (viewState.showSpecialPromotionBS) {
        SpecialPromotionBottomSheet(
            specialPromotionUi = viewState.specialPromotion,
            onDismissRequest = { viewModel.closeSpecialPromotionBottomSheet() },
            onButtonClick = { }
        )
    }

    AnimatedVisibility(viewState.showUnratedProductsBS) {
        UnratedProductsBottomSheet(
            sectionUnratedProducts = viewState.sectionUnratedProducts,
            onProductRatingChanged = { product, rating ->
                viewModel.changeUnratedProductRating(product, rating)
            },
            onDispose = {
                viewModel.closeUnratedProductsBottomSheet()
            }
        )
    }




}