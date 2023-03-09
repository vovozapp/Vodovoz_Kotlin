package com.vodovoz.app.ui.model

import com.vodovoz.app.R
import com.vodovoz.app.ui.base.content.itemadapter.Item

data class OrderUI(
    val id: Long?,
    val price: Int?,
    val orderStatusUI: OrderStatusUI?,
    val address: String?,
    val date: String,
    val productUIList: List<ProductUI>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_slider_order
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is OrderUI) return false

        return id == item.id
    }
}