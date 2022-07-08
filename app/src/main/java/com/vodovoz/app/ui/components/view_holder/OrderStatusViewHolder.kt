package com.vodovoz.app.ui.components.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderOrderStatusBinding
import com.vodovoz.app.ui.model.OrderStatusUI

class OrderStatusViewHolder(
    private val binding: ViewHolderOrderStatusBinding
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            binding.check.isChecked = !binding.check.isChecked
            orderStatusUI.isChecked = binding.check.isChecked
        }

        binding.check.setOnCheckedChangeListener { _, isChecked ->
            orderStatusUI.isChecked = isChecked
        }
    }

    private lateinit var orderStatusUI: OrderStatusUI

    fun onBind(orderStatusUI: OrderStatusUI) {
        this.orderStatusUI = orderStatusUI

        binding.status.text = orderStatusUI.statusName
        binding.check.isChecked = orderStatusUI.isChecked
    }
}