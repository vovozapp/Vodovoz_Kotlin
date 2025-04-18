package com.vodovoz.app.feature.cart.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vodovoz.app.R
import com.vodovoz.app.design_system.ExtendedTheme
import com.vodovoz.app.design_system.composables.button.CartCounterButton
import com.vodovoz.app.design_system.composables.chip.VodovozColorChipSmall
import com.vodovoz.app.feature.cart.model.CartItemUi
import com.vodovoz.app.feature.cart.model.ProductRestrictionUi

@Composable
fun CartItemCard(
    modifier: Modifier = Modifier,
    cartItem: CartItemUi,
    onClick: (CartItemUi) -> Unit,
    onLikeClick: (CartItemUi) -> Unit,
    onIncrement: (CartItemUi) -> Unit,
    onDecrement: (CartItemUi) -> Unit,
    onRemove: (CartItemUi) -> Unit,
) {
    val context = LocalContext.current

    val isPresent = cartItem.label?.name?.contains("подарок", true) == true
    val isAvailable = cartItem.canBuy && cartItem.leftItems > 0
    val restriction = cartItem.restriction

    Row(
        modifier = modifier.clickable(
            onClick = { onClick(cartItem) },
            indication = null,
            interactionSource = null
        )
    ) {
        Column(
            modifier = Modifier.width(76.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(cartItem.image).crossfade(true).build(),
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 11.dp)
                    .size(76.dp)
                    .alpha(if (isAvailable) 1f else 0.5f)
            )

            val label = cartItem.label
            if (label != null && isAvailable) {
                VodovozColorChipSmall(
                    color = label.background,
                    text = label.name,
                    textColor = label.color
                )
            }
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    text = cartItem.productName,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )

                if (restriction != ProductRestrictionUi.NO_FAVORITES && restriction != ProductRestrictionUi.FULL_RESTRICTION && restriction != ProductRestrictionUi.NO_FAVORITES_QUANTITY) {
                    Icon(
                        painter = painterResource(id = if (cartItem.isFavorite) R.drawable.ic_filled_like else R.drawable.ic_like),
                        contentDescription = null,
                        tint = if (cartItem.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(
                                onClick = { onLikeClick(cartItem) },
                                indication = null,
                                interactionSource = null
                            )
                    )
                }
            }

            Row {
                Column(modifier = Modifier.weight(1f)) {
                    if (cartItem.articleText.isNotEmpty()) {
                        Text(
                            text = cartItem.articleText,
                            color = MaterialTheme.colorScheme.surfaceTint,
                            style = ExtendedTheme.typography.labelSmallVariant
                        )
                    }
                    if (cartItem.depositText.isNotEmpty() && isAvailable) {
                        Text(
                            text = AnnotatedString.fromHtml(cartItem.depositText),
                            color = MaterialTheme.colorScheme.surfaceTint,
                            style = ExtendedTheme.typography.labelSmallVariant
                        )
                    }
                    if (!isAvailable) {
                        Text(
                            text = stringResource(R.string.product_end),
                            color = MaterialTheme.colorScheme.error,
                            style = ExtendedTheme.typography.labelSmallVariant
                        )
                    }
                }
                if (
                    !isAvailable
                    && restriction != ProductRestrictionUi.FULL_RESTRICTION
                    && restriction != ProductRestrictionUi.NO_DELETE
                ) {
                    TrashButton(modifier = Modifier.padding(start = 4.dp)) { onRemove(cartItem) }
                }
            }

            if (isAvailable) {
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            modifier = Modifier.alignByBaseline(),
                            text = cartItem.priceText,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                        if (cartItem.hasDiscount) {
                            val discountText = stringResource(R.string.price, cartItem.basePrice)
                            Text(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .alignByBaseline(),
                                text = discountText,
                                color = MaterialTheme.colorScheme.surfaceTint,
                                style = ExtendedTheme.typography.labelExtraSmallVariant.copy(
                                    textDecoration = TextDecoration.LineThrough
                                ),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))


                    if (
                        !isPresent && isAvailable
                        && restriction != ProductRestrictionUi.NO_QUANTITY
                        && restriction != ProductRestrictionUi.NO_FAVORITES_QUANTITY
                        && restriction != ProductRestrictionUi.FULL_RESTRICTION
                    ) {
                        CartCounterButton(
                            cartQuantity = cartItem.quantity,
                            catalogQuantity = cartItem.leftItems,
                            onPlusClick = {
                                onIncrement(cartItem)
                            },
                            onMinusClick = {
                                onDecrement(cartItem)
                            },
                            onTrashClick = {
                                onRemove(cartItem)
                            }
                        )
                    } else if (
                        restriction != ProductRestrictionUi.FULL_RESTRICTION && restriction != ProductRestrictionUi.NO_DELETE) {
                        TrashButton(
                            onClick = { onRemove(cartItem) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrashButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    FilledTonalButton(
        modifier = modifier
            .width(50.dp)
            .height(38.dp),
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_trash),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}