package com.vodovoz.app.feature.home.viewholders.homeorders

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.order_slider.OrdersSliderConfig
import com.vodovoz.app.ui.model.OrderUI

data class HomeOrders(
    val id : Int,
    val items: List<OrderUI>,
    val orderSliderConfig: OrdersSliderConfig = OrdersSliderConfig(
        containTitleContainer = true
    )
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_slider_order
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeOrders) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomeOrders) return false

        return this == item
    }
}
