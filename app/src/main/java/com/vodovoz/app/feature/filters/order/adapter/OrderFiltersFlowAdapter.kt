package com.vodovoz.app.feature.filters.order.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.OrderFilterUI


class OrderFiltersFlowAdapter(
    private val onCheckItem: (String, Boolean) -> Unit,
    ) : ItemAdapter() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItemViewHolder<out Item> {
        return when (viewType) {
            OrderFilterUI.ORDER_STATUS_FILTER_VIEW_TYPE ->
                OrderFiltersFlowViewHolder(
                    getViewFromInflater(
                        R.layout.view_holder_order_status,
                        parent
                    ),
                    onCheckItem
                )
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}