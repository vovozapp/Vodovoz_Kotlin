package com.vodovoz.app.ui.model

import com.vodovoz.app.data.model.common.OrderStatus

class OrderUI(
    val id: Long,
    val price: Int,
    val status: OrderStatus,
    val address: String
)