package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderOrderBinding
import com.vodovoz.app.ui.model.OrderUI
import com.vodovoz.app.ui.view_holder.OrdersSliderViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class OrderSliderAdapter(
    private val onOrderClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<OrdersSliderViewHolder>() {

    var orderUIList = listOf<OrderUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = OrdersSliderViewHolder(
        binding = ViewHolderSliderOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onOrderClickSubject = onOrderClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: OrdersSliderViewHolder,
        position: Int
    ) = holder.onBind(orderUIList[position])

    override fun getItemCount() = orderUIList.size

}