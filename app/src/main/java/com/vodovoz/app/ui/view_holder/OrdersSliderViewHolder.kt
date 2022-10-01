package com.vodovoz.app.ui.view_holder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderOrderBinding
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.OrderUI
import io.reactivex.rxjava3.subjects.PublishSubject

class OrdersSliderViewHolder(
    private val binding: ViewHolderSliderOrderBinding,
    private val onOrderClickSubject: PublishSubject<Long>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { onOrderClickSubject.onNext(orderUI.id!!) }
    }

    private lateinit var orderUI: OrderUI

    fun onBind(orderUI: OrderUI) {
        this.orderUI = orderUI

        binding.tvStatus.text = orderUI.orderStatusUI?.statusName
        binding.tvAddress.text = StringBuilder()
            .append("№")
            .append(orderUI.id)
            .append(" на ")
            .append(orderUI.date)
            .append("\n")
            .append(orderUI.address)
            .toString()
        binding.tvPrice.setPriceText(orderUI.price!!)
        orderUI.orderStatusUI?.let {
            binding.tvAction.setBackgroundColor(ContextCompat.getColor(context, orderUI.orderStatusUI.color))
            binding.tvStatus.setTextColor(ContextCompat.getColor(context, orderUI.orderStatusUI.color))
            binding.imgStatus.setImageDrawable(ContextCompat.getDrawable(context, orderUI.orderStatusUI.image))
            binding.imgStatus.setColorFilter(ContextCompat.getColor(context, orderUI.orderStatusUI.color))
        }
    }

}