package com.vodovoz.app.ui.components.adapter.bannerSliderAdapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderBannerBinding
import com.vodovoz.app.ui.model.BannerUI

class BannerSliderViewHolder(
    private val binding: ViewHolderSliderBannerBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var bannerUI: BannerUI

    fun onBind(bannerUI: BannerUI) {
        this.bannerUI = bannerUI

        Glide
            .with(context)
            .load(bannerUI.detailPicture)
            .into(binding.detailPicture)
    }

}