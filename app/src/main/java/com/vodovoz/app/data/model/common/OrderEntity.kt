package com.vodovoz.app.data.model.common

class OrderEntity(
    val id: Long? = null,
    val price: Int? = null,
    val status: OrderStatusEntity? = null,
    val address: String? = null,
    val productEntityList: List<ProductEntity> = listOf()
)