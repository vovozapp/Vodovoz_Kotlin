package com.vodovoz.app.ui.components.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.data.model.common.BannerActionEntity
import com.vodovoz.app.databinding.ViewHolderSliderBannerBinding
import com.vodovoz.app.ui.components.view_holder.BannerSliderViewHolder
import com.vodovoz.app.ui.model.BannerUI
import io.reactivex.rxjava3.subjects.PublishSubject

class BannersSliderAdapter(
    private val onBannerClickSubject: PublishSubject<BannerActionEntity>,
) : RecyclerView.Adapter<BannerSliderViewHolder>() {

    var bannerUIList = listOf<BannerUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = BannerSliderViewHolder(
        binding = ViewHolderSliderBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onBannerClickSubject = onBannerClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: BannerSliderViewHolder,
        position: Int
    ) = holder.onBind(bannerUIList[position])

    override fun getItemCount() = bannerUIList.size

}