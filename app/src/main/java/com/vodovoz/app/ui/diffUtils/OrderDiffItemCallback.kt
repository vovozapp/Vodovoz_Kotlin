package com.vodovoz.app.ui.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.OrderUI

class OrderDiffItemCallback: DiffUtil.ItemCallback<OrderUI>() {

    override fun areItemsTheSame(oldItem: OrderUI, newItem: OrderUI) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: OrderUI, newItem: OrderUI): Boolean {

        if (oldItem.orderStatusUI != newItem.orderStatusUI) return false
        if (oldItem.address != newItem.address) return false
        if (oldItem.price != newItem.price) return false

        return true
    }

}