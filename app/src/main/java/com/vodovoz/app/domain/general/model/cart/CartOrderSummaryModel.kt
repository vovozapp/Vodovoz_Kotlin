package com.vodovoz.app.domain.general.model.cart

data class CartOrderSummaryModel(
    val finalPriceText: String,
    val productsPriceText: String,
    val discountText: String,
    val depositText: String,
    val presentText: String
)
