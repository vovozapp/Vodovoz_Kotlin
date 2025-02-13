package com.vodovoz.app.ui.model

data class OrderListUI(
    val filters: List<OrderFilterUI>,
    val orders: List<OrderUI>,
    val title: String = "",
    val message: String = "",
)
