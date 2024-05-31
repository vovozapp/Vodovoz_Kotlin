package com.vodovoz.app.feature.productdetail.viewholders.detailprices.inner

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderPriceBinding
import com.vodovoz.app.ui.model.PriceUI

class PricesFlowViewHolder(view: View) : ItemViewHolder<PriceUI>(view) {

    private val binding: ViewHolderPriceBinding = ViewHolderPriceBinding.bind(view)

    override fun bind(item: PriceUI) {
        super.bind(item)

        binding.tvPrice.text = StringBuilder()
            .append(item.currentPrice.toInt())
            .append(" руб")
            .toString()

        binding.tvPriceCondition.text = StringBuilder()
            .append("От ")
            .append(item.requiredAmount)
            .append(" шт")
            .toString()
    }
}