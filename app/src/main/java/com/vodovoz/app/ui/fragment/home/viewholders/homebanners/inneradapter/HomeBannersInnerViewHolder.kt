package com.vodovoz.app.ui.fragment.home.viewholders.homebanners.inneradapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderBannerBinding
import com.vodovoz.app.ui.model.BannerUI

class HomeBannersInnerViewHolder(
    view: View,
    private val clickListener: HomeBannersSliderClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: ViewHolderSliderBannerBinding = ViewHolderSliderBannerBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onBannerClick(item.actionEntity)
        }
    }

    fun bind(banner: BannerUI) {
        Glide
            .with(itemView.context)
            .load(banner.detailPicture)
            .into(binding.imgPicture)
    }

    private fun getItemByPosition(): BannerUI? {
        return (bindingAdapter as? HomeBannersInnerAdapter)?.getItem(bindingAdapterPosition)
    }
}