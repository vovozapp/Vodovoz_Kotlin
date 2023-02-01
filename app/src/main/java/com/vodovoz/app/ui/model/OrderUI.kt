package com.vodovoz.app.ui.model

data class OrderUI(
    val id: Long?,
    val price: Int?,
    val orderStatusUI: OrderStatusUI?,
    val address: String?,
    val date: String,
    val productUIList: List<ProductUI>
)