package com.vodovoz.app.ui.fragment.home.viewholders.homebrands.inneradapter

import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderBrandBinding
import com.vodovoz.app.ui.base.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.BrandUI

class HomeBrandsInnerViewHolder(
    view: View,
    private val clickListener: HomeBrandsSliderClickListener
) : ItemViewHolder<BrandUI>(view) {

    private val binding: ViewHolderSliderBrandBinding = ViewHolderSliderBrandBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onBrandClick(item.id)
        }
    }

    override fun bind(item: BrandUI) {
        super.bind(item)
        Glide
            .with(itemView.context)
            .load(item.detailPicture)
            .into(binding.imgPicture)
    }

    private fun getItemByPosition(): BrandUI? {
        return (bindingAdapter as? HomeBrandsInnerAdapter)?.getItem(bindingAdapterPosition) as? BrandUI
    }
}