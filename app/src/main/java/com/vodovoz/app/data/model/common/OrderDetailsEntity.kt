package com.vodovoz.app.data.model.common

class OrderDetailsEntity(
    val id: Long? = null,
    val dateOrder: String = "",
    val dateDelivery: String = "",
//    val productsPrice: Int = 0,
//    val deliveryPrice: Int = 0,
    val depositPrice: Int = 0,
    val totalPrice: Int = 0,
    val userFirstName: String = "",
    val userSecondName: String = "",
    val userPhone: String = "",
    val deliveryTimeInterval: String = "",
    val isPayed: Boolean = false,
    val payMethod: String = "",
    val payUri: String = "",
    val status: OrderStatusEntity? = null,
    val address: String = "",
    val productEntityList: List<ProductEntity> = listOf(),
    val driverId: String? = null,
    val driverName: String? = null,
    val orderPricesEntityList: List<OrderPricesEntity> = listOf()
)