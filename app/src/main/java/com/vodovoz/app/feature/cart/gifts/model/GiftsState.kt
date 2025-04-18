package com.vodovoz.app.feature.cart.gifts.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.feature.cart.model.CartPresentItemUi

@Immutable
data class GiftsState(
    val value: String = "",
    val button: ColorfulButtonUi = ColorfulButtonUi.Empty,
    val gifts: List<CartPresentItemUi> = emptyList(),
    val currentGift: CartPresentItemUi = CartPresentItemUi.Empty
)
