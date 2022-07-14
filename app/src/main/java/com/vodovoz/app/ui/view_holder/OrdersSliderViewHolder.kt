package com.vodovoz.app.ui.view_holder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderOrderBinding
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.extensions.TextViewExtensions.setDrawableColor
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

        binding.status.text = orderUI.orderStatusUI?.statusName
        binding.address.text = orderUI.address
        binding.price.setPriceText(orderUI.price!!)
        binding.status.setDrawableColor(orderUI.orderStatusUI!!.color)
        binding.action.setBackgroundColor(ContextCompat.getColor(context, orderUI.orderStatusUI.color))
        binding.status.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(context, orderUI.orderStatusUI.image), null, null, null
        )
    }

}