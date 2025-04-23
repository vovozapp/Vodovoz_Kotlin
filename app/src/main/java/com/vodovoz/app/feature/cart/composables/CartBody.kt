package com.vodovoz.app.feature.cart.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.domain.general.model.cart.CartOrderSummaryUi
import com.vodovoz.app.feature.all.orders.detail.composables.ExampleOfProgressBar
import com.vodovoz.app.feature.cart.model.CartButtonUi
import com.vodovoz.app.feature.cart.model.CartItemUi
import com.vodovoz.app.feature.cart.model.CartPresentUi
import com.vodovoz.app.feature.cart.model.CartPromoButtonUi

@Suppress("NonSkippableComposable")
@Composable
fun CartBody(
    modifier: Modifier = Modifier,
    countCartItemsText: String,
    cartItems: List<CartItemUi>,
    cartPresent: CartPresentUi?,
    cartOrderSummary: CartOrderSummaryUi,
    bottlesButton: CartButtonUi?,
    promotionCodeButton: CartPromoButtonUi?,
    presentButton: CartButtonUi?,
    onClearCartClick: () -> Unit,
    onRemoveCartItem: (CartItemUi) -> Unit,
    onIncrementCartItem: (CartItemUi) -> Unit,
    onDecrementCartItem: (CartItemUi) -> Unit,
    onLikeCartItem: (CartItemUi) -> Unit,
    onCartItemClick: (CartItemUi) -> Unit,
    onPromotionCodeButtonClick: (CartPromoButtonUi) -> Unit,
    onPresentButtonClick: () -> Unit,
    onBottlesButtonClick: () -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp, start = 16.dp, end = 16.dp),
    ) {
        item {
            cartPresent?.let {
                CartPresentCard(
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .animateContentSize(),
                    currentCartPrice = cartOrderSummary.productsPriceText.filter { c ->
                        c.isDigit()
                    }.toIntOrNull() ?: 0,
                    present = cartPresent,
                    onChoosePresentClick = onPresentButtonClick
                )
            }
        }

        item {
            Row(modifier = Modifier.padding(bottom = 24.dp)) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = countCartItemsText,
                    color = MaterialTheme.colorScheme.surfaceTint,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.clear_cart),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable(onClick = onClearCartClick)
                )
            }
        }

        itemsIndexed(items = cartItems, key = { _, item -> item.id }) { i, cartItem ->
            Column(
                modifier = Modifier.animateItem(
                    fadeInSpec = null,
                    fadeOutSpec = null,
                    placementSpec = tween(90, easing = LinearEasing)
                )
            ) {
                CartItemCard(
                    cartItem = cartItem,
                    onClick = onCartItemClick,
                    onLikeClick = onLikeCartItem,
                    onDecrement = onDecrementCartItem,
                    onIncrement = onIncrementCartItem,
                    onRemove = onRemoveCartItem
                )

                if (cartItems.lastIndex != i) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

            }

        }

        item {
            bottlesButton?.apply {
                CartButton(
                    modifier = Modifier.padding(top = 24.dp),
                    image = image,
                    name = name,
                    onClick = onBottlesButtonClick
                )
            }
        }

        item {
            promotionCodeButton?.apply {
                CartButton(
                    modifier = Modifier.padding(top = 24.dp),
                    image = image,
                    name = coupon.ifEmpty { title },
                    label = text.takeIf { txt -> txt.isNotEmpty() },
                    onClick = { onPromotionCodeButtonClick(promotionCodeButton) }
                )
            }
        }

        item {
            presentButton?.apply {
                CartButton(
                    modifier = Modifier.padding(top = 24.dp),
                    image = image,
                    name = name,
                    onClick = onPresentButtonClick
                )
            }
        }

        item {
            CartOrderSummaryColumn(
                modifier = Modifier.padding(top = 24.dp),
                orderSummary = cartOrderSummary
            )
        }

        item {
            VodovozButton(
                modifier = Modifier.padding(top = 24.dp),
                text = stringResource(id = R.string.place_order),
                onClick = {
                    /*TODO*/
                }
            )
        }
    }
}