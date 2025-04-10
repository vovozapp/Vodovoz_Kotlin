package com.vodovoz.app.feature.productdetail

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
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

    val floatingButtonProgress by animateFloatAsState(
        targetValue = if (viewState.hideFloatingButton) 1f else 0f,
        animationSpec = tween(easing = LinearEasing, durationMillis = 100),
        label = "floatingButtonProgress"
    )

    //todo - move in viewModel
    //val (price, oldPrice) = productDetails.firstPrice.run { price.roundToInt() to oldPrice.roundToInt() }

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
            ProductBottomFloatingButton(
                modifier = Modifier.graphicsLayer {
                    translationY = lerp(0f, size.height, floatingButtonProgress)
                },
                isLoading = viewState.buttonIsLoading,
                cartQuantity = productDetails.cartQuantity,
                //todo - move to viewModel
                //calculateProductPrice(
                //     productDetails.cartQuantity,
                //     productDetails.prices
                // ).roundToInt()
                //
                totalPrice = 123,
                //todo - put value
                //oldPrice = oldPrice,
                //price = price,
                oldPrice = 123,
                price = 124,
                //todo - put left gift
                giftText = "0",
                isAvailable = productDetails.isAvailable,
                analogButton = viewState.buttons.analogButton,
                onIncrementProduct = {
                    viewModel.incrementCart()
                },
                onDecrementProduct = {
                    viewModel.decrementCart()
                },
                onAnalogClick = {
                    viewModel.navigateToProductsCollection()
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        ProductDetailsBody(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .verticalScroll(rememberScrollState())
                .padding(bottom = paddingValues.calculateBottomPadding()),
            productDetails = productDetails,
            comments = viewState.comments,
            quantityButtonIsLoading = viewState.buttonIsLoading,
            productCartQuantity = productDetails.cartQuantity,
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
            onIncrementProduct = {
                viewModel.incrementCart()
            },
            onDecrementProduct = {
                viewModel.decrementCart()
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

            onBrandClick = { brandItem ->
                viewModel.navigateToBrandProducts(brandItem)
            },
            onCategoryClick = { categoryItem ->
                viewModel.navigateToCategory(categoryItem)
            },
            onCopyArticleNumberClick = {
                viewModel.copyArticleNumber()
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

    if (viewState.showMultiBottomSheet) {
        MultiProductBottomSheet(
            cartQuantity = productDetails.cartQuantity,
            firstPrice = productDetails.firstPrice,
            prices = productDetails.prices,
            buttonIsLoading = viewState.buttonIsLoading,
            onDismissRequest = {
                viewModel.hideMultiBottomSheet()
            },
            onCartQuantityChange = { newCartQuantity ->
                viewModel.changeToCart(newCartQuantity)
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