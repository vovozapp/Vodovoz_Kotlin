package com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter

import android.view.View
import androidx.core.content.ContextCompat
import com.vodovoz.app.databinding.ViewHolderSliderOrderBinding
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.OrderUI

class HomeOrdersInnerViewHolder(
    view: View,
    private val clickListener: HomeOrdersSliderClickListener
) : ItemViewHolder<OrderUI>(view) {

    private val binding: ViewHolderSliderOrderBinding = ViewHolderSliderOrderBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onOrderClick(item.id)
        }
    }

    override fun bind(item: OrderUI) {
        super.bind(item)

        binding.tvStatus.text = item.orderStatusUI?.statusName
        binding.tvAddress.text = StringBuilder()
            .append("№")
            .append(item.id)
            .append(" на ")
            .append(item.date)
            .append("\n")
            .append(item.address)
            .toString()
        item.price?.let {
            binding.tvPrice.setPriceText(item.price)
        }
        item.orderStatusUI?.let {
            binding.tvAction.setBackgroundColor(ContextCompat.getColor(itemView.context, item.orderStatusUI.color))
            binding.tvStatus.setTextColor(ContextCompat.getColor(itemView.context, item.orderStatusUI.color))
            binding.imgStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, item.orderStatusUI.image))
            binding.imgStatus.setColorFilter(ContextCompat.getColor(itemView.context, item.orderStatusUI.color))
        }
    }

    private fun getItemByPosition(): OrderUI? {
        return (bindingAdapter as? HomeOrdersInnerAdapter)?.getItem(bindingAdapterPosition) as? OrderUI
    }
}