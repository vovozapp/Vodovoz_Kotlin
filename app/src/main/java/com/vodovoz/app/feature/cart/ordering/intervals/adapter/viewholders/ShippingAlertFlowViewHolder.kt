package com.vodovoz.app.feature.cart.ordering.intervals.adapter.viewholders

import android.view.View
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderShippingAlertBinding
import com.vodovoz.app.ui.model.ShippingAlertUI

class ShippingAlertFlowViewHolder(
    view: View,
    clickListener: ((Item) -> Unit),
) : ItemViewHolder<ShippingAlertUI>(view) {

    private val binding: ViewHolderShippingAlertBinding = ViewHolderShippingAlertBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener(item)
        }
    }

    override fun bind(item: ShippingAlertUI) {
        super.bind(item)

        binding.tvName.text = item.name
    }

}