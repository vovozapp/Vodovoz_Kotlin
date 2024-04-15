package com.vodovoz.app.data.model.common

class OrderEntity(
    val id: Long? = null,
    val price: Int? = null,
    val status: OrderStatusEntity? = null,
    val address: String? = null,
    val date: String = "",
    val productEntityList: List<ProductEntity> = listOf(),
    val repeatOrder: Boolean = true,
    val interval: String = "",
)