package com.vodovoz.app.feature.home.viewholders.homebanners.inneradapter

import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderSliderBannerBinding
import com.vodovoz.app.ui.model.BannerUI

class HomeBannersInnerViewHolder(
    view: View,
    private val clickListener: HomeBannersSliderClickListener
) : ItemViewHolder<BannerUI>(view) {

    private val binding: ViewHolderSliderBannerBinding = ViewHolderSliderBannerBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onBannerClick(item.actionEntity)
        }

        binding.advIncludeCard.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onBannerAdvClick(item.advEntity)
        }
    }

    override fun bind(item: BannerUI) {
        super.bind(item)
        Glide
            .with(itemView.context)
            .load(item.detailPicture)
            .into(binding.imgPicture)

        binding.advIncludeCard.root.isVisible = item.advEntity != null
        binding.advIncludeCard.advTv.text = item.advEntity?.titleHeader
    }

    private fun getItemByPosition(): BannerUI? {
        return (bindingAdapter as? HomeBannersInnerAdapter)?.getItem(bindingAdapterPosition) as? BannerUI
    }
}