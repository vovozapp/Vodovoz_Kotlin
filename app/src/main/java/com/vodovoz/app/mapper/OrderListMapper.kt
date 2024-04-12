package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.OrderListEntity
import com.vodovoz.app.mapper.OrderFilterMapper.mapToUI
import com.vodovoz.app.mapper.OrderMapper.mapToUI
import com.vodovoz.app.ui.model.OrderListUI

object OrderListMapper {

    fun OrderListEntity.mapToUI(): OrderListUI {
        return OrderListUI(
            filters = orderFilters.mapToUI(),
            orders = orders.mapToUI()
        )
    }
}