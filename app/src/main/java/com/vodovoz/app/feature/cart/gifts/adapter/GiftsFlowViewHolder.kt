package com.vodovoz.app.feature.cart.gifts.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderGiftBinding
import com.vodovoz.app.ui.model.ProductUI

class GiftsFlowViewHolder(
    view: View,
    clickListener: GiftsFlowClickListener
) : ItemViewHolder<ProductUI>(view) {

    private val binding: ViewHolderGiftBinding = ViewHolderGiftBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?:return@setOnClickListener
            clickListener.onProductClick(item)
        }
    }

    override fun bind(item: ProductUI) {
        super.bind(item)

        binding.tvName.text = item.name
        Glide.with(itemView.context)
            .load(item.detailPicture)
            .into(binding.imgPicture)
    }
}