package com.vodovoz.app.feature.filters.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.OrderFilterUI
import com.vodovoz.app.util.extensions.debugLog


class OrderFiltersFlowAdapter(
    private val onCheckItem: (String, Boolean) -> Unit,
) : RecyclerView.Adapter<OrderFiltersFlowViewHolder>() {

    var items = mutableListOf<OrderFilterUI>()
        set(value) {
            debugLog { "OrderFiltersFlowAdapter.set(value)" }
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): OrderFiltersFlowViewHolder {
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

    override fun onBindViewHolder(holder: OrderFiltersFlowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return OrderFilterUI.ORDER_STATUS_FILTER_VIEW_TYPE
    }


    fun getViewFromInflater(layoutId: Int, parent: ViewGroup): View {
        return LayoutInflater
            .from(parent.context)
            .inflate(layoutId, parent, false)
    }
}