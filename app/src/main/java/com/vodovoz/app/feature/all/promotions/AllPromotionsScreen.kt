package com.vodovoz.app.feature.all.promotions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.all.promotions.composables.AdvertisingInfoBottomSheet
import com.vodovoz.app.feature.all.promotions.composables.AllPromotionsBody

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

    Column(modifier = Modifier.fillMaxSize()) {
        VodovozTopBar(
            onBack = { viewModel.navigateBack() },
            title = stringResource(id = R.string.promotions)
        )


        AllPromotionsBody(
            sections = viewState.sections,
            currentSection = viewState.currentSection,
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

    val bottomSheetState = rememberModalBottomSheetState()
    if (viewState.showAdvertisingBottomSheet) {
        AdvertisingInfoBottomSheet(
            advertising = viewState.currentAdvertising,
            onDismissRequest = { viewModel.closeAdvertisingBottomSheet() },
            state = bottomSheetState
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
}