package com.vodovoz.app.ui.components.adapter.bannerSliderAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderBannerBinding
import com.vodovoz.app.ui.model.BannerUI

class BannerSliderAdapter() : RecyclerView.Adapter<BannerSliderViewHolder>() {

    var bannerUIList = listOf<BannerUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BannerSliderViewHolder(
        binding = ViewHolderSliderBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: BannerSliderViewHolder,
        position: Int
    ) = holder.onBind(bannerUIList[position])

    override fun getItemCount() = bannerUIList.size

}