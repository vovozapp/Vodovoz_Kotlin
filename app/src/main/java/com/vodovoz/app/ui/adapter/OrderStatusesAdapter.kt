package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderOrderStatusBinding
import com.vodovoz.app.ui.model.OrderStatusUI
import com.vodovoz.app.ui.view_holder.OrderStatusViewHolder

class OrderStatusesAdapter : RecyclerView.Adapter<OrderStatusViewHolder>() {

    var orderStatusUIList = listOf<OrderStatusUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = OrderStatusViewHolder(
        binding = ViewHolderOrderStatusBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    )

    override fun onBindViewHolder(
        holder: OrderStatusViewHolder,
        position: Int
    ) = holder.onBind(orderStatusUIList[position])

    override fun getItemCount() = orderStatusUIList.size

}