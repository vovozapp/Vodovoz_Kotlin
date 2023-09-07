package com.vodovoz.app.feature.filters.order.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.OrderStatusUI


class OrderStatusesFlowAdapter : ItemAdapter() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItemViewHolder<out Item> {
        return when (viewType) {
            OrderStatusUI.ORDER_STATUS_VIEW_TYPE ->
                OrderStatusFlowViewHolder(
                    getViewFromInflater(
                        R.layout.view_holder_order_status,
                        parent
                    )
                )
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }

        }
    }

}