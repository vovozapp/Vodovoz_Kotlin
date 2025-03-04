package com.vodovoz.app.feature.all.promotions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.all.promotions.composables.AdvertisingInfoBottomSheet
import com.vodovoz.app.feature.all.promotions.composables.AllPromotionsBody
import com.vodovoz.app.feature.all.promotions.composables.PromotionsLoadingPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("NonSkippableComposable")
@Composable
fun AllPromotionsScreen(
    viewModel: AllPromotionsFlowViewModel,
    viewState: AllPromotionsFlowViewModel.AllPromotionsState,
    navController: NavController,
) {
    val lazyPagingPromotions = viewState.pagedPromotions.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .consumeWindowInsets(WindowInsets.systemBars)
    ) {
        VodovozTopBar(
            onBack = { viewModel.navigateBack() },
            title = viewState.title.ifEmpty { stringResource(id = R.string.promotions) }
        )

        when (viewState.uiState) {
            AllPromotionsFlowViewModel.UiState.Error -> {}
            AllPromotionsFlowViewModel.UiState.Loading -> {
                PromotionsLoadingPlaceholder()
            }

            AllPromotionsFlowViewModel.UiState.Success -> {
                AllPromotionsBody(
                    categories = viewState.categories,
                    currentCategory = viewState.currentCategory,
                    lazyPagingPromotions = lazyPagingPromotions,
                    lazyListState = lazyListState,
                    onSectionSelect = { section ->
                        viewModel.selectSection(section)
                    },
                    onAdvertisingClick = { promotionUi ->
                        viewModel.showAdvertisingBottomSheet(promotionUi)
                    },
                    onPromotionClick = { promotion ->
                        viewModel.navigateToPromotionDetails(promotion)
                    }
                )
            }
        }

    }

    val bottomSheetState = rememberModalBottomSheetState()
    if (viewState.showAdvertisingBottomSheet) {
        AdvertisingInfoBottomSheet(
            advertising = viewState.currentAdvertising,
            onDismissRequest = { viewModel.closeAdvertisingBottomSheet() },
            state = bottomSheetState
        )
    }

    LifecycleEffect {
        viewModel.observeEvent().collect { event ->
            when (event) {
                AllPromotionsFlowViewModel.AllPromotionsEvent.ScrollTop -> {
                    lazyListState.animateScrollToItem(0)
                }

                is AllPromotionsFlowViewModel.AllPromotionsEvent.GoToProductDetails -> {
                    navController.navigate(
                        AllPromotionsFragmentDirections.actionToPromotionDetailFragment(
                            event.promotionId
                        )
                    )
                }

                AllPromotionsFlowViewModel.AllPromotionsEvent.GoBack -> {
                    navController.popBackStack()
                }
            }
        }
    }
}