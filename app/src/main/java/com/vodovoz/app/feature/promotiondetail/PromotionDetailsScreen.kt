package com.vodovoz.app.feature.promotiondetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.promotiondetail.composables.PromotionDetailsBody
import com.vodovoz.app.feature.promotiondetail.composables.PromotionDetailsLoadingPlaceholder

@Suppress("NonSkippableComposable")
@Composable
fun PromotionDetailsScreen(
    viewModel: PromotionDetailFlowViewModel,
    viewState: PromotionDetailFlowViewModel.PromotionDetailFlowState,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        VodovozTopBar(onBack = { viewModel.navigateBack() }, title = "")

        when (viewState.uiState) {
            PromotionDetailFlowViewModel.UiState.Error -> {

            }

            PromotionDetailFlowViewModel.UiState.Loading -> {
                PromotionDetailsLoadingPlaceholder()
            }

            PromotionDetailFlowViewModel.UiState.Success -> {
                PromotionDetailsBody(
                    promotionDetails = viewState.promotionDetails,
                    products = viewState.products,
                    productsLoadStates = viewState.productsLoadStates,
                    productsTitle = viewState.productsTitle,
                    onHyperlinkClick = { url ->
                        viewModel.navigateToWebView(url)
                    },
                    onProductClick = { product ->
                        viewModel.navigateToProductDetails(product)
                    },
                    onProductAnalogsClick = { product ->
                        viewModel.navigateToProductAnalogs(product)
                    },
                    onProductLike = { product ->
                        viewModel.changeProductFavorite(product)
                    },
                    onProductSee = { index ->
                        viewModel.notifyPagingProducts(index)
                    },
                    onIncrementProductToCart = { product ->
                        viewModel.incrementProductToCart(product)
                    },
                    onDecrementProductToCart = { product ->
                        viewModel.decrementProductToCart(product)
                    }
                )
            }
        }
    }
}