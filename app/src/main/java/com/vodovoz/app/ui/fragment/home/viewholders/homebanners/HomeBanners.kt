package com.vodovoz.app.ui.fragment.home.viewholders.homebanners

import com.vodovoz.app.R
import com.vodovoz.app.ui.fragment.home.adapter.Item
import com.vodovoz.app.ui.model.BannerUI

data class HomeBanners(
    val id : Int,
    val items: List<BannerUI>,
    val bannerRatio: Double? = null
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_slider_banner
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeBanners) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomeBanners) return false

        return this == item
    }
}