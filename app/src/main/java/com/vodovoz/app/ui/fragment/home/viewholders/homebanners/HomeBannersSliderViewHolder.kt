package com.vodovoz.app.ui.fragment.home.viewholders.homebanners

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.FragmentSliderBannerBinding
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.model.BannerUI

class HomeBannersSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: FragmentSliderBannerBinding = FragmentSliderBannerBinding.bind(view)

    init {

    }

    fun bind(items: HomeBanners) {

    }

}