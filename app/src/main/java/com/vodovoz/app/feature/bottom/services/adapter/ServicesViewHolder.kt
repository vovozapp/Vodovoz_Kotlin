package com.vodovoz.app.feature.bottom.services.adapter

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderServiceDetailBinding
import com.vodovoz.app.ui.model.ServiceUI

class ServicesViewHolder(
    view: View,
    clickListener: ServicesClickListener
) : ItemViewHolder<ServiceUI>(view) {

    private val binding: ViewHolderServiceDetailBinding = ViewHolderServiceDetailBinding.bind(view)

    init {
        binding.btnMoreInfo.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onItemClick(item)
        }
    }

    override fun bind(item: ServiceUI) {
        super.bind(item)

        binding.tvName.text = item.name
        binding.tvDetails.text = item.detail
    }
}