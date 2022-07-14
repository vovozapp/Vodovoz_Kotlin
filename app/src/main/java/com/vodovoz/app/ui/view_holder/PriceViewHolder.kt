package com.vodovoz.app.ui.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderPriceBinding
import com.vodovoz.app.ui.model.PriceUI

class PriceViewHolder(
    private val binding: ViewHolderPriceBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(priceUI: PriceUI) {
        binding.price.text = StringBuilder()
            .append(priceUI.price)
            .append(" руб")
            .toString()

        binding.priceCondition.text = StringBuilder()
            .append("От ")
            .append(priceUI.requiredAmount)
            .append(" шт")
            .toString()
    }

}