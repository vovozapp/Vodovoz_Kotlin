package com.vodovoz.app.domain.general.model.cart

import com.vodovoz.app.domain.general.model.ColorfulButtonModel

data class CartPresentModel(
    val id: Long,
    val title: String,
    val description: String,
    val image: String,
    val leftToGift: Int,
    val button: ColorfulButtonModel?,
    val popupWindow: CartPresentPopupWindowModel?
)
