package com.vodovoz.app.data.model.common

import com.vodovoz.app.R

enum class OrderStatusEntity(
    val id: String,
    val color: Int,
    val statusName: String,
    val image: Int
) {
    IN_PROCESSING(
        id = "D",
        color = R.color.color_status_in_processing,
        statusName = "В обработке",
        image = R.drawable.ic_order_in_processing
    ),
    IN_DELIVERY(
        id = "E",
        color = R.color.color_status_in_delivery,
        statusName = "Передан в службу доставки",
        image = R.drawable.ic_order_in_delivery
    ),
    COMPLETED(
        id = "F",
        color = R.color.color_status_completed,
        statusName = "Выполнен",
        image = R.drawable.ic_order_completed
    ),
    ACCEPTED(
        id = "N",
        color = R.color.color_status_accepted,
        statusName = "Принят",
        image = R.drawable.ic_order_accepted
    ),
    CANCELED(
        id = "R",
        color = R.color.color_status_canceled,
        statusName = "Отменен",
        image = R.drawable.ic_order_canceled
    ),
    DELIVERED(
        id = "S",
        color = R.color.color_status_delivered,
        statusName = "Доставлен",
        image = R.drawable.ic_order_in_delivery
    );

    companion object {

        fun fromId(id: String) = when(id) {
            "D" -> IN_PROCESSING
            "E" -> IN_DELIVERY
            "F" -> COMPLETED
            "N" -> ACCEPTED
            "R" -> CANCELED
            "S" -> DELIVERED
            else -> throw Exception()
        }

    }

}
