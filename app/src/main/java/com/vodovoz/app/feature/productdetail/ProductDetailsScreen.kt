package com.vodovoz.app.feature.productdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.design_system.composables.button.ProductFloatingButton
import com.vodovoz.app.feature.productdetail.composables.ProductDetailTopBar
import com.vodovoz.app.feature.productdetail.composables.ProductDetailsBody
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.ui.model.ProductDetailUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.calculateProductPrice
import kotlin.math.roundToInt

@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsScreen(
    productDetail: ProductDetailUI,
    buyWithProductUIList: List<ProductUI>,
    comments: List<CommentUI>,
    category: CategoryUI,
    showDetailPreviewText: Boolean,
    showAllProperties: Boolean,
    viewedProductUIList: List<ProductUI>,
    articleNumber: String,
    deposit: Int,
    searchWords: List<String>,
    productCartQuantity: Int,
    buttonIsLoading: Boolean,
    hideFloatingButton: Boolean,
    onFloatingButtonChange: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    onAllPropertiesShow: () -> Unit,
    onDetailPreviewTextShowOrHide: () -> Unit,
    onProductImageClick: () -> Unit,
    onAddToCart: () -> Unit,
    onProductMinus: () -> Unit,
    onProductPlus: () -> Unit,
    onNavigateToCart: () -> Unit,
) {
    Scaffold(
        topBar = {
            ProductDetailTopBar(
                onNavigationClick = onNavigateBack,
                onLikeClick = onLikeClick,
                onShareClick = onShareClick,
                isFavoriteProduct = productDetail.isFavorite
            )
        },
        bottomBar = {
            val (price, oldPrice) = productDetail.priceUIList.first().run {
                currentPrice.roundToInt() to oldPrice.roundToInt()
            }

            AnimatedVisibility(!hideFloatingButton) {
                ProductFloatingButton(
                    isLoading = buttonIsLoading,
                    productCartQuantity = productCartQuantity,
                    totalPrice = calculateProductPrice(
                        productCartQuantity,
                        productDetail.priceUIList
                    ).roundToInt(),
                    oldPrice = oldPrice,
                    price = price,
                    //todo - put left gift
                    leftToGift = 0,
                    onProductPlus = onProductPlus,
                    onProductMinus = onProductMinus,
                    onCartClick = onNavigateToCart,
                    onAddToCartClick = onAddToCart
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        ProductDetailsBody(
            modifier = Modifier.padding(paddingValues),
            productDetailUi = productDetail,
            comments = comments,
            category = category,
            deposit = deposit,
            searchWords = searchWords,
            quantityButtonIsLoading = buttonIsLoading,
            productCartQuantity = productCartQuantity,
            viewedProductUIList = viewedProductUIList,
            buyWithProductUIList = buyWithProductUIList,
            showDetailPreviewText = showDetailPreviewText,
            showAllProperties = showAllProperties,
            articleNumber = articleNumber,
            hideFloatingButton = hideFloatingButton,
            onFloatingButtonChange = onFloatingButtonChange,
            onAllPropertiesShow = onAllPropertiesShow,
            onDetailPreviewTextShowOrHide = onDetailPreviewTextShowOrHide,
            onProductImageClick = { onProductImageClick() },
            onProductPlus = onProductPlus,
            onProductMinus = onProductMinus,
            onAddToCart = onAddToCart,
            onNavigateToCart = onNavigateToCart
        )
    }
}