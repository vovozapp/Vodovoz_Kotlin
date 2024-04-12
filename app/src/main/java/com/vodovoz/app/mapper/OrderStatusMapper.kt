package com.vodovoz.app.mapper

import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.OrderStatusEntity
import com.vodovoz.app.ui.model.OrderStatusUI

object OrderStatusMapper {

    fun OrderStatusEntity.mapToUI(): OrderStatusUI = when(id) {
        "D" -> OrderStatusUI(
            id = id,
            statusName = statusName,
            color = R.color.color_status_in_processing,
            image = R.drawable.ic_order_in_processing
            )
        "E" -> OrderStatusUI(
            id = id,
            statusName = statusName,
            color = R.color.color_status_in_delivery,
            image = R.drawable.ic_order_in_delivery
        )
        "F" -> OrderStatusUI(
            id = id,
            statusName = statusName,
            color = R.color.color_status_completed,
            image = R.drawable.ic_order_completed
        )
        "N" -> OrderStatusUI(
            id = id,
            statusName = statusName,
            color = R.color.color_status_accepted,
            image = R.drawable.ic_check_round
        )
        "R" -> OrderStatusUI(
            id = id,
            statusName = statusName,
            color = R.color.color_status_canceled,
            image = R.drawable.ic_order_canceled
        )
        "S" -> OrderStatusUI(
            id = id,
            statusName = statusName,
            color = R.color.color_status_delivered,
            image = R.drawable.ic_order_in_delivery
        )
        else -> OrderStatusUI(
            id = id,
            statusName = statusName,
            color = R.color.color_status_canceled,
            image = R.drawable.ic_order_canceled
        )
    }
}