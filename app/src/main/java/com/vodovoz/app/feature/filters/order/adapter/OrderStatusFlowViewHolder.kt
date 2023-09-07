package com.vodovoz.app.feature.filters.order.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderBottleNameBinding
import com.vodovoz.app.databinding.ViewHolderOrderStatusBinding
import com.vodovoz.app.ui.model.OrderStatusUI

class OrderStatusFlowViewHolder(
    view: View
) : ItemViewHolder<OrderStatusUI>(view) {

    private val binding: ViewHolderOrderStatusBinding = ViewHolderOrderStatusBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            binding.cbChecked.isChecked = !binding.cbChecked.isChecked
            orderStatusUI.isChecked = binding.cbChecked.isChecked
        }

        binding.cbChecked.setOnCheckedChangeListener { _, isChecked ->
            orderStatusUI.isChecked = isChecked
        }
    }

    private lateinit var orderStatusUI: OrderStatusUI

    override fun bind(item: OrderStatusUI) {
        super.bind(item)
        this.orderStatusUI = orderStatusUI

        binding.tvName.text = orderStatusUI.statusName
        binding.cbChecked.isChecked = orderStatusUI.isChecked
    }
}