package com.vodovoz.app.ui.fragment.home.viewholders.homeorders.inneradapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.OrderUI

class HomeOrdersDiffUtilCallback: DiffUtil.ItemCallback<OrderUI>() {

    override fun areContentsTheSame(oldItem: OrderUI, newItem: OrderUI): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: OrderUI, newItem: OrderUI): Boolean {
        return oldItem.id == newItem.id
    }
}