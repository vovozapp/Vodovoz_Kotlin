package com.vodovoz.app.feature.filters.order.adapter

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderOrderStatusBinding
import com.vodovoz.app.ui.model.OrderStatusUI

class OrderStatusViewHolder(
    private val binding: ViewHolderOrderStatusBinding
) : RecyclerView.ViewHolder(binding.root) {

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

    fun onBind(orderStatusUI: OrderStatusUI) {
        this.orderStatusUI = orderStatusUI

        binding.tvName.text = orderStatusUI.statusName
        binding.cbChecked.isChecked = orderStatusUI.isChecked
    }
}