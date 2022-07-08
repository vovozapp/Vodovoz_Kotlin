package com.vodovoz.app.ui.model

import com.vodovoz.app.data.model.common.OrderStatusEntity

class OrderUI(
    val id: Long?,
    val price: Int?,
    val orderStatusUI: OrderStatusUI?,
    val address: String?,
    val productUIList: List<ProductUI>
)