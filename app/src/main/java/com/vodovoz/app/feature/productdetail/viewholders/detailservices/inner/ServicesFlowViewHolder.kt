package com.vodovoz.app.feature.productdetail.viewholders.detailservices.inner

import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderServiceBinding
import com.vodovoz.app.ui.model.ServiceUI

class ServicesFlowViewHolder(view: View) : ItemViewHolder<ServiceUI>(view) {

    private val binding: ViewHolderServiceBinding = ViewHolderServiceBinding.bind(view)

    override fun bind(item: ServiceUI) {
        super.bind(item)

        binding.tvName.text = item.name
        binding.tvPrice.text = item.price

        Glide.with(itemView.context)
            .load(item.detailPicture)
            .into(binding.imgPicture)
    }
}