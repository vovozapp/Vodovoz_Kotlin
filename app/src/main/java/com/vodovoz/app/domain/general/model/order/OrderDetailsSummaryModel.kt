package com.vodovoz.app.domain.general.model.order

data class OrderDetailsSummaryModel(
    val finalPriceText: String,
    val productsPriceText: String,
    val depositText: String,
    val deliveryText: String,
    val parkingText: String,
)
