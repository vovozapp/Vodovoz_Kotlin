package com.vodovoz.app.domain.general.model.cart

data class CartDetailsModel(
    val title: String,
    val countText: String,
    val items: List<CartItemModel>,
    val orderSummary: CartOrderSummaryModel,
    val present: CartPresentModel?,
    val bottlesButton: CartButtonModel?,
    val promotionalCodeButton: CartPromoButtonModel?,
    val presentButton: CartButtonModel?,
)
