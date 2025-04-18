package com.vodovoz.app.domain.general.model.cart

data class CartPromoButtonModel(
    val title: String,
    val text: String,
    val coupon: String,
    val textColor: String,
    val image: String,
    val id: String,
    val popupWindow: CartPromoPopupWindowModel,

)
