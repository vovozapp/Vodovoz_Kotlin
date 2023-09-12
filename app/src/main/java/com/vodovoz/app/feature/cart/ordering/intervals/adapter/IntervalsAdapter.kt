package com.vodovoz.app.feature.cart.ordering.intervals.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.viewholders.GenderFlowViewHolder
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.viewholders.PayMethodViewHolder
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.viewholders.ShippingAlertFlowViewHolder
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.viewholders.ShippingIntervalFlowViewHolder

class IntervalsAdapter(
    private val clickListener: ((Item)->Unit)
): ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            R.layout.view_holder_shipping_interval -> {
                ShippingIntervalFlowViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.view_holder_shipping_alert -> {
                ShippingAlertFlowViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.view_holder_pay_method -> {
                PayMethodViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.view_holder_gender -> {
                GenderFlowViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}