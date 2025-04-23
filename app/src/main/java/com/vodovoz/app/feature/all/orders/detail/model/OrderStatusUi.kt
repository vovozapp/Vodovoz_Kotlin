package com.vodovoz.app.feature.all.orders.detail.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.order.OrderStatusModel

@Immutable
data class OrderStatusUi(
    val name: String,
) {
    companion object {
        val Empty = OrderStatusUi("")
    }
}

fun List<OrderStatusModel>.mapToUi(): List<OrderStatusUi> {
    return map { it.toUi() }
}

fun OrderStatusModel.toUi(): OrderStatusUi {
    return OrderStatusUi(name)
}