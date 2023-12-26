package com.vodovoz.app.feature.all.orders.detail.prices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderOrderDetailsPricesBinding
import com.vodovoz.app.databinding.ViewHolderOrderDetailsPricesBoldBinding
import com.vodovoz.app.ui.model.OrderPricesUI

class OrderPricesAdapter : ListAdapter<OrderPricesUI, OrderPricesAdapter.OrderPricesViewHolder>(OrderPricesDiffCallback()) {

    sealed class OrderPricesViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        class OrderPricesNormalViewHolder(val binding: ViewHolderOrderDetailsPricesBinding) :
            OrderPricesViewHolder(binding.root)

        class OrderPricesBoldViewHolder(val binding: ViewHolderOrderDetailsPricesBoldBinding) :
            OrderPricesViewHolder(binding.root)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderPricesViewHolder {
        val viewHolder = when(viewType){
            IS_BOLD_HOLDER -> {
                val binding = ViewHolderOrderDetailsPricesBoldBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OrderPricesViewHolder.OrderPricesBoldViewHolder(binding)
            }
            IS_NORMAL_HOLDER -> {
                val binding = ViewHolderOrderDetailsPricesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OrderPricesViewHolder.OrderPricesNormalViewHolder(binding)
            }
            else -> throw RuntimeException("Unknown view type: $viewType")
        }
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == currentList.lastIndex) {
            IS_BOLD_HOLDER
        } else {
            IS_NORMAL_HOLDER
        }
    }

    override fun onBindViewHolder(holder: OrderPricesViewHolder, position: Int) {
        val priceItem = getItem(position)
        when(holder) {
            is OrderPricesViewHolder.OrderPricesNormalViewHolder -> {
                holder.binding.labelPriceTextView.text = priceItem.name
                holder.binding.priceTextView.text = priceItem.result
            }
            is OrderPricesViewHolder.OrderPricesBoldViewHolder -> {
                holder.binding.labelPriceTextView.text = priceItem.name
                holder.binding.priceTextView.text = priceItem.result
            }
        }
    }

    companion object {
        private const val IS_NORMAL_HOLDER = 753
        private const val IS_BOLD_HOLDER = 754
    }
}