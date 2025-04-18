package com.vodovoz.app.feature.cart.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.design_system.model.LabelWithColorUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.model.cart.CartItemModel

@Immutable
data class CartItemUi(
    val id: Long,
    val productId: Long,
    val productName: String,
    val isFavorite: Boolean,
    val isHit: Boolean,
    val canBuy: Boolean,
    val discountPercentsText: String,
    val discountPrice: Float,
    val priceText: String,
    val currentPrice: Float,
    val basePrice: Float,
    val quantity: Int,
    val depositText: String,
    val articleText: String,
    val image: String,
    val leftItems: Int,
    val label: LabelWithColorUi?,
    val hasDiscount: Boolean,
    val restriction: ProductRestrictionUi
)


@JvmName("withUpdatedFavoritesCartItem")
fun List<CartItemUi>.withUpdatedFavorites(favorites: Map<Long, Boolean>): List<CartItemUi> {
    return map { cartItem ->
        cartItem.copy(isFavorite = favorites[cartItem.productId] ?: cartItem.isFavorite)
    }
}

@JvmName("withUpdatedCartCartItem")
fun List<CartItemUi>.withUpdatedCart(cart: Map<Long, Int>): List<CartItemUi> {
    return map { cartItem ->
        cartItem.copy(quantity = cart[cartItem.productId] ?: cartItem.quantity)
    }
}


fun List<CartItemModel>.mapToUi(): List<CartItemUi> {
    return map { it.toUi() }
}

fun CartItemModel.toUi(): CartItemUi {
    return CartItemUi(
        id = id,
        productId = productId,
        productName = productName,
        isFavorite = isFavorite,
        isHit = isHit,
        canBuy = canBuy,
        discountPercentsText = discountPercentsText,
        discountPrice = discountPrice,
        priceText = priceText,
        currentPrice = currentPrice,
        basePrice = basePrice,
        quantity = quantity,
        depositText = depositText,
        articleText = articleText,
        image = image,
        leftItems = leftItems,
        label = label?.toUi(),
        hasDiscount = hasDiscount,
        restriction = ProductRestrictionUi.fromCode(restrictionsCode)
    )
}
