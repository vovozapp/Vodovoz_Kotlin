package com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter

import android.view.View
import androidx.core.content.ContextCompat
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderSliderOrderBinding
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.OrderUI

class HomeOrdersInnerViewHolder(
    view: View,
    private val clickListener: HomeOrdersSliderClickListener,
    private val repeatOrderClickListener: (Long) -> Unit,
) : ItemViewHolder<OrderUI>(view) {

    private val binding: ViewHolderSliderOrderBinding = ViewHolderSliderOrderBinding.bind(view)

    init {
        binding.infoLayout.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onOrderClick(item.id)
        }
        binding.tvAction.setOnClickListener {
            val item = item ?: return@setOnClickListener
            if (item.orderStatusUI != null && item.orderStatusUI.id == "F" && item.repeatOrder) {
                repeatOrderClickListener(item.id ?: throw RuntimeException("Order's id is null"))
            } else {
                clickListener.onOrderClick(item.id)
            }
        }
    }

    override fun bind(item: OrderUI) {
        super.bind(item)

        binding.tvStatus.text = item.orderStatusUI?.statusName

        binding.tvAction.text = if (item.repeatOrder) {
            "Повторить"
        } else {
            "Подробнее"
        }

        val sb = StringBuilder()
            .append("№")
            .append(item.id)
            .append(" от ")
            .append(item.date)
        if (item.interval.isNotEmpty()) {
            sb.append(", ${item.interval}")
        }
        sb.append("\n")
            .append(item.address)

        binding.tvAddress.text = sb.toString()

            item.price?.let {
                binding.tvPrice.setPriceText(item.price)
            }
        item.orderStatusUI?.let {
            binding.tvAction.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    item.orderStatusUI.color
                )
            )
            binding.tvStatus.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    item.orderStatusUI.color
                )
            )
            binding.imgStatus.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    item.orderStatusUI.image
                )
            )
            binding.imgStatus.setColorFilter(
                ContextCompat.getColor(
                    itemView.context,
                    item.orderStatusUI.color
                )
            )
        }
    }
}