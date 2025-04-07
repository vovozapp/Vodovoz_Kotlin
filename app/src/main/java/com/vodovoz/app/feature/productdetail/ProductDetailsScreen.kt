package com.vodovoz.app.feature.productdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.design_system.composables.button.ProductBottomFloatingButton
import com.vodovoz.app.feature.productdetail.composables.MultiProductBottomSheet
import com.vodovoz.app.feature.productdetail.composables.PresentBottomSheet
import com.vodovoz.app.feature.productdetail.composables.ProductDetailsBody
import com.vodovoz.app.feature.productdetail.composables.ProductDetailsTopBar
import com.vodovoz.app.util.calculateProductPrice
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsScreen(
    viewState: ProductDetailsFlowViewModel.ProductDetailsState,
    viewModel: ProductDetailsFlowViewModel,
) {
    val productDetails = viewState.productDetails

    Scaffold(
        topBar = {
            ProductDetailsTopBar(
                onNavigationClick = {
                    viewModel.navigateBack()
                },
                onLikeClick = {
                    viewModel.changeFavorite()
                },
                onShareClick = {
                    viewModel.share()
                },
                isFavoriteProduct = productDetails.isFavorite
            )
        },
        bottomBar = {
            val (price, oldPrice) = productDetails.firstPrice.run { price.roundToInt() to oldPrice.roundToInt() }

            AnimatedVisibility(visible = !viewState.hideFloatingButton) {
                ProductBottomFloatingButton(
                    isLoading = viewState.buttonIsLoading,
                    cartQuantity = viewState.cartQuantity,
                    totalPrice = calculateProductPrice(
                        viewState.cartQuantity,
                        productDetails.prices
                    ).roundToInt(),
                    oldPrice = oldPrice,
                    price = price,
                    //todo - put left gift
                    giftText = "0",
                    isAvailable = productDetails.isAvailable,
                    analogButton = viewState.buttons.analogButton,
                    onProductPlus = {
                        viewModel.incrementCart()
                    },
                    onProductMinus = {
                        viewModel.decrementCart()
                    },
                    onAddToCartClick = {
                        viewModel.changeCart(productDetails.id, 1, 0)
                    },
                    onAnalogClick = {
                        viewModel.navigateToProductsCollection()
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        ProductDetailsBody(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            productDetails = productDetails,
            comments = viewState.comments,
            quantityButtonIsLoading = viewState.buttonIsLoading,
            productCartQuantity = viewState.cartQuantity,
            showAllProperties = viewState.showAllProperties,
            sectionSimilarProducts = viewState.sectionSimilarProducts,
            sectionAccessory = viewState.sectionAccessory,
            showDetailText = viewState.showDetailText,
            buttons = viewState.buttons,
            onFloatingButtonChange = { show ->
                viewModel.changeFloatingButton(show)
            },
            onAllPropertiesShow = {
                viewModel.showAllProperties()
            },
            onDetailPreviewTextShowOrHide = {
                viewModel.showOrHideDetailText()
            },
            onProductMediaClick = { media ->
                viewModel.navigateByMedia(media)
            },
            onProductPlus = {
                viewModel.incrementCart()
            },
            onProductMinus = {
                viewModel.decrementCart()
            },
            onAddToCart = {
                viewModel.changeCart(productDetails.id, 1, 0)
            },
            onCartClick = {
                viewModel.navigateToCart()
            },
            onAnalogButtonClick = {
                viewModel.navigateToProductsCollection()
            },
            onPreOrderButtonClick = {
                viewModel.navigateToPreOrder()
            },
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
            },
            onShowAllCommentsClick = {
                viewModel.showAllComments()
            },
            onQueryClick = { query ->
                viewModel.navigateToSearch(query)
            },
            onProductClick = { product ->
                viewModel.navigateToProductDetails(product)
            },
            onProductLikeClick = { product ->
                viewModel.changeFavorite(product)
            },
            onCategoryClick = { categoryItem ->
                viewModel.navigateToCategory(categoryItem)
            },
            onCopyArticleNumberClick = {
                viewModel.copyArticleNumber()
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