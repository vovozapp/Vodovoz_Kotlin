package com.vodovoz.app.feature.cart.bottles.adapter

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderBottleNameBinding
import com.vodovoz.app.ui.model.BottleUI

class BottleNameFlowViewHolder(
    view: View,
    clickListener: OnBottleClickListener,
) : ItemViewHolder<BottleUI>(view) {

    private val binding: ViewHolderBottleNameBinding = ViewHolderBottleNameBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onBottleClick(item)
        }
    }

    override fun bind(item: BottleUI) {
        super.bind(item)

        binding.name.text = item.name
    }

}