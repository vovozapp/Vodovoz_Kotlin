package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.OrderStatusEntity
import com.vodovoz.app.ui.model.OrderStatusUI

object OrderStatusMapper {

    fun OrderStatusEntity.mapToUI(): OrderStatusUI = when(this) {
        OrderStatusEntity.ACCEPTED -> OrderStatusUI.ACCEPTED
        OrderStatusEntity.COMPLETED -> OrderStatusUI.COMPLETED
        OrderStatusEntity.CANCELED -> OrderStatusUI.CANCELED
        OrderStatusEntity.DELIVERED -> OrderStatusUI.DELIVERED
        OrderStatusEntity.IN_DELIVERY -> OrderStatusUI.IN_DELIVERY
        OrderStatusEntity.IN_PROCESSING -> OrderStatusUI.IN_PROCESSING
    }
}