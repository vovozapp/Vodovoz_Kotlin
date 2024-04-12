package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.OrderDetailsEntity
import com.vodovoz.app.data.model.common.OrderPricesEntity
import com.vodovoz.app.mapper.OrderStatusMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.OrderDetailsUI
import com.vodovoz.app.ui.model.OrderPricesUI


fun OrderDetailsEntity.mapToUI() = OrderDetailsUI(
    id = id,
    dateOrder = dateOrder,
    dateDelivery = dateDelivery,
//        productsPrice = productsPrice,
//        deliveryPrice = deliveryPrice,
    depositPrice = depositPrice,
    totalPrice = totalPrice,
    userFirstName = userFirstName,
    userSecondName = userSecondName,
    userPhone = userPhone,
    deliveryTimeInterval = deliveryTimeInterval,
    isPayed = isPayed,
    payMethod = payMethod,
    payUri = payUri,
    status = status?.mapToUI(),
    address = address,
    productUIList = productEntityList.mapToUI(),
    driverId = driverId,
    driverName = driverName,
    driverUrl = driverUrl,
    orderPricesUIList = orderPricesEntityList.mapToUI(),
    repeatOrder = repeatOrder,
)

fun List<OrderPricesEntity>.mapToUI() = this.map { it.mapToUI() }

fun OrderPricesEntity.mapToUI() = OrderPricesUI(
    name = name,
    result = result
)