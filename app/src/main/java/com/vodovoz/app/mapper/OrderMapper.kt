package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.OrderEntity
import com.vodovoz.app.mapper.OrderStatusMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.OrderUI

object OrderMapper {

    fun List<OrderEntity>.mapToUI(): List<OrderUI> = mutableListOf<OrderUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun OrderEntity.mapToUI() = OrderUI(
        id = id,
        price = price,
        orderStatusUI = status?.mapToUI(),
        address = address,
        date = date,
        productUIList = productEntityList.mapToUI()
    )

}