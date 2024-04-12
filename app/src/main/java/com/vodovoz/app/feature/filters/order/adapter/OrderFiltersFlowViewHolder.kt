package com.vodovoz.app.feature.filters.order.adapter

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderOrderStatusBinding
import com.vodovoz.app.ui.model.OrderFilterUI

class OrderFiltersFlowViewHolder(
    view: View,
    private val onCheckItem: (String, Boolean) -> Unit,
) : ItemViewHolder<OrderFilterUI>(view) {

    private val binding: ViewHolderOrderStatusBinding = ViewHolderOrderStatusBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            onCheckItem(item.id, !binding.cbChecked.isChecked)
        }

        binding.cbChecked.setOnClickListener {
            val item = item ?: return@setOnClickListener
            onCheckItem(item.id, !binding.cbChecked.isChecked)
        }

    }

    override fun bind(item: OrderFilterUI) {
        super.bind(item)

        binding.tvName.text = item.name
        binding.cbChecked.isChecked = item.isChecked
    }
}