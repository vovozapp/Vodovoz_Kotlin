package com.vodovoz.app.feature.all.orders.detail.model

import com.vodovoz.app.domain.general.model.order.OrderDetailsSummaryModel

data class OrderDetailsSummaryUi(
    val finalPriceText: String,
    val productsPriceText: String,
    val depositText: String,
    val deliveryText: String,
    val parkingText: String,
) {
    companion object {
        val Empty = OrderDetailsSummaryUi(
            "",
            "",
            "",
            "",
            ""
        )
    }
}

fun OrderDetailsSummaryModel.toUi(): OrderDetailsSummaryUi{
    return OrderDetailsSummaryUi(
        finalPriceText, productsPriceText, depositText, deliveryText, parkingText
    )
}
