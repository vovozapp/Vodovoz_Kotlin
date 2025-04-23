package com.vodovoz.app.domain.general.model.order

import com.vodovoz.app.domain.general.model.ColorfulButtonModel

data class OrderDetailsModel(
    val title: String,
    val subtitle: String,
    val currentStatus: OrderStatusModel,
    val statuses: List<OrderStatusModel>,
    val topButtons: List<OrderDetailsButtonModel>,
    val bottomButtons: List<ColorfulButtonModel>,
    val orderSummary: OrderDetailsSummaryModel,
    val productsTitle: String,
    val products: List<OrderProductModel>
)
