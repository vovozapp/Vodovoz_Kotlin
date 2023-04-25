package com.vodovoz.app.feature.profile.viewholders.models

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.OrderUI

data class ProfileOrders(
    val id: Int = 3,
    val data: List<OrderUI>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.item_profile_order_slider
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ProfileOrders) return false

        return id == item.id
    }

}