package com.vodovoz.app.feature.addresses.adapter.viewholders

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderAddressesTypeTitleBinding
import com.vodovoz.app.ui.model.AddressFlowTitle

class AddressTitleFlowViewHolder(
    view: View,
//    clickListener: AddressesClickListener
) : ItemViewHolder<AddressFlowTitle>(view) {

    private val binding: ViewHolderAddressesTypeTitleBinding =
        ViewHolderAddressesTypeTitleBinding.bind(view)

    override fun bind(item: AddressFlowTitle) {
        super.bind(item)

        binding.tvTitle.text = item.title
    }

}