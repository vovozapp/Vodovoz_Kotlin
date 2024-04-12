package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.OrderFilterEntity
import com.vodovoz.app.ui.model.OrderFilterUI

object OrderFilterMapper {

    fun List<OrderFilterEntity>.mapToUI() = map { it.mapToUI() }

    fun OrderFilterEntity.mapToUI() = OrderFilterUI(
        id = id,
        name = name
    )
}