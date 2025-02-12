package com.vodovoz.app.feature.productdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.design_system.composables.button.ProductFloatingButton
import com.vodovoz.app.feature.productdetail.composables.MultiProductBottomSheet
import com.vodovoz.app.feature.productdetail.composables.PresentBottomSheet
import com.vodovoz.app.feature.productdetail.composables.ProductDetailTopBar
import com.vodovoz.app.feature.productdetail.composables.ProductDetailsBody
import com.vodovoz.app.util.calculateProductPrice
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsScreen(
    viewState: ProductDetailsFlowViewModel.ProductDetailsState,
    viewModel: ProductDetailsFlowViewModel,
    onFloatingButtonChange: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    onAllPropertiesShow: () -> Unit,
    onDetailPreviewTextShowOrHide: () -> Unit,
    onProductImageClick: () -> Unit,
    onAddToCart: () -> Unit,
    onNavigateToCart: () -> Unit,
) {
    val productDetails = viewState.productDetails

    Scaffold(
        topBar = {
            ProductDetailTopBar(
                onNavigationClick = onNavigateBack,
                onLikeClick = onLikeClick,
                onShareClick = onShareClick,
                isFavoriteProduct = productDetails.isFavorite
            )
        },
        bottomBar = {
            val (price, oldPrice) = productDetails.firstPrice.run { price.roundToInt() to oldPrice.roundToInt() }

            AnimatedVisibility(!viewState.hideFloatingButton) {
                ProductFloatingButton(
                    isLoading = viewState.buttonIsLoading,
                    cartQuantity = viewState.cartQuantity,
                    totalPrice = calculateProductPrice(
                        viewState.cartQuantity,
                        productDetails.prices
                    ).roundToInt(),
                    oldPrice = oldPrice,
                    price = price,
                    //todo - put left gift
                    leftToGift = 0,
                    isAvailable = productDetails.isAvailable,
                    analogButton = viewState.buttons.analogButton,
                    onProductPlus = {
                        viewModel.incrementCart()
                    },
                    onProductMinus = {
                        viewModel.decrementCart()
                    },
                    onCartClick = onNavigateToCart,
                    onAddToCartClick = onAddToCart,
                    onAnalogClick = {}
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        ProductDetailsBody(
            modifier = Modifier.padding(paddingValues),
            comments = viewState.comments,
            quantityButtonIsLoading = viewState.buttonIsLoading,
            productCartQuantity = viewState.cartQuantity,
            showAllProperties = viewState.showAllProperties,
            onFloatingButtonChange = onFloatingButtonChange,
            onAllPropertiesShow = onAllPropertiesShow,
            onDetailPreviewTextShowOrHide = onDetailPreviewTextShowOrHide,
            onProductImageClick = {
                onProductImageClick()
            },
            onProductPlus = {
                viewModel.incrementCart()
            },
            onProductMinus = {
                viewModel.decrementCart()
            },
            onAddToCart = onAddToCart,
            onNavigateToCart = onNavigateToCart,
            productDetails = productDetails,
            sectionSimilarProducts = viewState.sectionSimilarProducts,
            sectionAccessory = viewState.sectionAccessory,
            showDetailText = viewState.showDetailText,
            buttons = viewState.buttons,
            onAnalogButtonClick = {},
            onPreOrderButtonClick = {},
            onPresentButtonClick = {
                viewModel.showPresentBottomSheet()
            },
            onMultiButtonClick = {
                viewModel.showMultiBottomSheet()
            },
            onPresentBlockButtonClick = {
                viewModel.showPresentBlockBottomSheet()
            },
            onAboutProductClick = {
                viewModel.navigateToAboutProduct()
            }
        )
    }

    if (viewState.showMultiBottomSheet) {
        MultiProductBottomSheet(
            cartQuantity = viewState.cartQuantity,
            firstPrice = productDetails.firstPrice,
            prices = productDetails.prices,
            buttonIsLoading = viewState.buttonIsLoading,
            onDismissRequest = { viewModel.hideMultiBottomSheet() },
            onCartQuantityChange = { newCartQuantity ->
                viewModel.changeCart(
                    productDetails.id,
                    newCartQuantity,
                    viewState.cartQuantity
                )
            },
            onPlus = {
                viewModel.incrementCart()

            },
            onMinus = {
                viewModel.decrementCart()
            }
        )
    }

    val presentButton = viewState.buttons.blockButton
    if (viewState.showPresentBottomSheet && presentButton != null) {
        PresentBottomSheet(
            data = presentButton.data,
            button = presentButton.buyButton,
            onDismissRequest = { viewModel.hidePresentBottomSheet() },
            onBuyButtonClick = { }
        )
    }

    val presentBlock = viewState.buttons.blockDesignButton
    if (viewState.showPresentBlockBottomSheet && presentBlock != null) {
        PresentBottomSheet(
            data = presentBlock.data,
            button = presentBlock.buyButton,
            onDismissRequest = { viewModel.hidePresentBlockBottomSheet() },
            onBuyButtonClick = { }
        )
    }
}