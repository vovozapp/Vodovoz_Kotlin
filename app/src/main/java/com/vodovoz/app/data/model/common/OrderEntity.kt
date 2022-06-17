package com.vodovoz.app.data.model.common

class OrderEntity(
    val id: Long,
    val price: Int,
    val status: OrderStatus,
    val address: String
)