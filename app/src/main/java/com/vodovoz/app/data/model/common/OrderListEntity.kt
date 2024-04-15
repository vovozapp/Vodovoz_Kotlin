package com.vodovoz.app.data.model.common

class OrderListEntity(
    val orderFilters: List<OrderFilterEntity> = listOf(),
    val orders: List<OrderEntity> = listOf(),
    val title: String = "",
    val message: String = "",
)