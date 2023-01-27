package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.ViewHolderSliderBannerBinding
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.ui.view_holder.BannerSliderViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class BannersSliderAdapter(
    private val onBannerClickSubject: PublishSubject<ActionEntity>,
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