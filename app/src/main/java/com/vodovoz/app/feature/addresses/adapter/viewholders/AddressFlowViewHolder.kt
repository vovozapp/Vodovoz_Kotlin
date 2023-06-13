package com.vodovoz.app.feature.addresses.adapter.viewholders

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderAddressBinding
import com.vodovoz.app.feature.addresses.adapter.AddressesClickListener
import com.vodovoz.app.ui.model.AddressUI

class AddressFlowViewHolder(
    view: View,
    clickListener: AddressesClickListener
) : ItemViewHolder<AddressUI>(view) {

    private val binding: ViewHolderAddressBinding =
        ViewHolderAddressBinding.bind(view)

    init {

        binding.imgEdit.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onEditClick(item)
        }
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onAddressClick(item)
        }
        binding.root.setOnLongClickListener {
            val item = item ?: return@setOnLongClickListener false
            clickListener.onDelete(item)
            false
        }
    }

    override fun bind(item: AddressUI) {
        super.bind(item)

        binding.tvAddress.text = item.newFullAddress

        /*binding.tvAddress.text = StringBuilder()
            .append(item.locality)
            .append(", ")
            .append(item.street)
            .append(", ")
            .append(item.house)
            .toString()*/

        binding.tvAdditionalInfo.text = StringBuilder()
            .append("Под:")
            .append(item.entrance)
            .append(", Этаж:")
            .append(item.floor)
            .append(", Кв:")
            .append(item.flat)
            .toString()
    }

}