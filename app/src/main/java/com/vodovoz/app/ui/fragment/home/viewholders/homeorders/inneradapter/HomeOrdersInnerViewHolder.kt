package com.vodovoz.app.ui.fragment.home.viewholders.homeorders.inneradapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderOrderBinding
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.OrderUI

class HomeOrdersInnerViewHolder(
    view: View,
    private val clickListener: HomeOrdersSliderClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: ViewHolderSliderOrderBinding = ViewHolderSliderOrderBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onOrderClick(item.id)
        }
    }

    fun bind(order: OrderUI) {
        binding.tvStatus.text = order.orderStatusUI?.statusName
        binding.tvAddress.text = StringBuilder()
            .append("№")
            .append(order.id)
            .append(" на ")
            .append(order.date)
            .append("\n")
            .append(order.address)
            .toString()
        order.price?.let {
            binding.tvPrice.setPriceText(order.price)
        }
        order.orderStatusUI?.let {
            binding.tvAction.setBackgroundColor(ContextCompat.getColor(itemView.context, order.orderStatusUI.color))
            binding.tvStatus.setTextColor(ContextCompat.getColor(itemView.context, order.orderStatusUI.color))
            binding.imgStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, order.orderStatusUI.image))
            binding.imgStatus.setColorFilter(ContextCompat.getColor(itemView.context, order.orderStatusUI.color))
        }
    }

    private fun getItemByPosition(): OrderUI? {
        return (bindingAdapter as? HomeOrdersInnerAdapter)?.currentList?.get(bindingAdapterPosition)
    }
}