package com.vodovoz.app.feature.promotiondetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.promotiondetail.composables.PromotionDetailsBody

@Suppress("NonSkippableComposable")
@Composable
fun PromotionDetailsScreen(
    viewModel: PromotionDetailFlowViewModel,
    viewState: PromotionDetailFlowViewModel.PromotionDetailFlowState,
    navController: NavController,
) {
    val lazyPagingProducts = viewState.products.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {
        VodovozTopBar(onBack = { viewModel.navigateBack() }, title = "")
        PromotionDetailsBody(
            promotionDetails = viewState.promotionDetails,
            lazyPagingProducts = lazyPagingProducts,
            productsTitle = viewState.productsTitle
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.observeEvent().collect { event ->
                when (event) {
                    PromotionDetailFlowViewModel.PromotionDetailEvent.GoBack -> navController.popBackStack()
                }

            }
        }
    }
}