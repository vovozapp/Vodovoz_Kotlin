package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.OrderDetailsEntity
import com.vodovoz.app.ui.mapper.OrderStatusMapper.mapToUI
import com.vodovoz.app.ui.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.OrderDetailsUI

object OrderDetailsMapper {

    fun OrderDetailsEntity.mapToUI() = OrderDetailsUI(
        id = id,
        dateOrder = dateOrder,
        dateDelivery = dateDelivery,
        productsPrice = productsPrice,
        deliveryPrice = deliveryPrice,
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
        productUIList = productEntityList.mapToUI()
    )

}