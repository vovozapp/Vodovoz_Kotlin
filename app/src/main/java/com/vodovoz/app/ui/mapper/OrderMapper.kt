package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.OrderEntity
import com.vodovoz.app.ui.mapper.OrderMapper.mapToUI
import com.vodovoz.app.ui.model.OrderUI

object OrderMapper {

    fun List<OrderEntity>.mapToUI(): List<OrderUI> = mutableListOf<OrderUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun OrderEntity.mapToUI() = OrderUI(
        id = id,
        price = price,
        status = status,
        address = address
    )

}