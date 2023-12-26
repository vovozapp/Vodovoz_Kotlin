package com.vodovoz.app.feature.all.orders.detail.prices

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.OrderPricesUI

class OrderPricesDiffCallback : DiffUtil.ItemCallback<OrderPricesUI>() {
    override fun areItemsTheSame(oldItem: OrderPricesUI, newItem: OrderPricesUI): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: OrderPricesUI, newItem: OrderPricesUI): Boolean {
        return oldItem == newItem
    }
}