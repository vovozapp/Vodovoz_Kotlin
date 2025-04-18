package com.vodovoz.app.feature.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.vodovoz.app.feature.cart.composables.CartBody
import com.vodovoz.app.feature.cart.composables.CartTopBar
import com.vodovoz.app.feature.cart.composables.PromotionCodeBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("NonSkippableComposable")
@Composable
fun CartScreen(viewModel: CartFlowViewModel, viewState: CartFlowViewModel.CartState) {
    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = viewState.showRefreshIndicator,
        onRefresh = { viewModel.refresh() },
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
        Column(modifier = Modifier.fillMaxSize()) {
            CartTopBar(title = viewState.title, onShareClick = { })
            CartBody(
                cartItems = viewState.cartItems,
                cartPresent = viewState.present,
                countCartItemsText = viewState.countText,
                bottlesButton = viewState.bottlesButton,
                presentButton = viewState.presentButton,
                promotionCodeButton = viewState.promotionalCodeButton,
                cartOrderSummary = viewState.orderSummary,
                onClearCartClick = {
                    viewModel.showClearCartDialog()
                },
                onDecrementCartItem = { cartItem ->
                    viewModel.decrementCartItem(cartItem)
                },
                onLikeCartItem = { cartItem ->
                    viewModel.changeFavorite(cartItem)
                },
                onIncrementCartItem = { cartItem ->
                    viewModel.incrementCartItem(cartItem)
                },
                onRemoveCartItem = { cartItem ->
                    viewModel.showTrashDialog(cartItem)
                },
                onCartItemClick = { cartItem ->
                    viewModel.navigateToProductDetails(cartItem)
                },
                onPresentButtonClick = {
                    viewModel.navigateToGifts()
                },
                onBottlesButtonClick = {
                    viewModel.navigateToAllBottles()
                },
                onPromotionCodeButtonClick = {
                    viewModel.showPromotionCodeBottomSheet()
                }
            )
        }
    }

    if (viewState.blockCart) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(0.4f))
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitPointerEvent(PointerEventPass.Initial)
                            .changes
                            .forEach { change ->
                                change.consume()
                            }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(45.dp),
                trackColor = Color.Transparent,
                color = MaterialTheme.colorScheme.primary,
                strokeCap = StrokeCap.Round,
                strokeWidth = 4.dp
            )
        }
    }

    if (viewState.showPromotionCodeBottomSheet && viewState.promotionalCodeButton != null) {
        PromotionCodeBottomSheet(
            info = viewState.promotionalCodeButton.popupWindow,
            promoCode = viewState.promoCode,
            onPromoCodeChange = { newValue ->
                viewModel.changePromoCode(newValue)
            },
            onDismiss = {
                viewModel.closePromoCodeBottomSheet()
            },
            onApplyPromoClick = {
                viewModel.applyPromoCode()
            }
        )
    }
}
