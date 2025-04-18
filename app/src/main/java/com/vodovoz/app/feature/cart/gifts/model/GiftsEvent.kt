package com.vodovoz.app.feature.cart.gifts.model

import com.vodovoz.app.feature.cart.model.CartPresentItemUi

sealed interface GiftsEvent {
    data class GoToCart(val currentGift: CartPresentItemUi) : GiftsEvent

    data object GoBack: GiftsEvent

}