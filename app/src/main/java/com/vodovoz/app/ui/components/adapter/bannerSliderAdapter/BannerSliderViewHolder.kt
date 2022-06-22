package com.vodovoz.app.ui.components.adapter.bannerSliderAdapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.data.model.common.BannerActionEntity
import com.vodovoz.app.databinding.ViewHolderSliderBannerBinding
import com.vodovoz.app.ui.model.BannerUI
import io.reactivex.rxjava3.subjects.PublishSubject

class BannerSliderViewHolder(
    private val binding: ViewHolderSliderBannerBinding,
    private val onBannerClickSubject: PublishSubject<BannerActionEntity>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onBannerClickSubject.onNext(bannerUI.bannerActionEntity!!)
        }
    }

    private lateinit var bannerUI: BannerUI

    fun onBind(bannerUI: BannerUI) {
        this.bannerUI = bannerUI

        Glide
            .with(context)
            .load(bannerUI.detailPicture)
            .into(binding.detailPicture)
    }

}