package com.vodovoz.app.feature.cart.ordering.intervals.adapter.viewholders

import android.view.View
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderShippingIntervalBinding
import com.vodovoz.app.ui.model.ShippingIntervalUI

class ShippingIntervalFlowViewHolder(
    view: View,
    clickListener: ((Item) -> Unit),
) : ItemViewHolder<ShippingIntervalUI>(view) {

    private val binding: ViewHolderShippingIntervalBinding =
        ViewHolderShippingIntervalBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener(item)
        }
    }

    override fun bind(item: ShippingIntervalUI) {
        super.bind(item)

        binding.tvName.text = item.name
    }

}