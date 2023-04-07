package com.vodovoz.app.feature.map.adapter

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderAddressResultBinding

class AddressResultFlowViewHolder(
    view: View,
    clickListener: AddressResultClickListener
) : ItemViewHolder<AddressResult>(view) {

    private val binding: ViewHolderAddressResultBinding = ViewHolderAddressResultBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onAddressClick(item)
        }
    }

    override fun bind(item: AddressResult) {
        super.bind(item)
        binding.address.text = item.text
    }
}