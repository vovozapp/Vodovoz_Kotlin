package com.vodovoz.app.ui.fragment.home.viewholders.homebanners

import com.vodovoz.app.ui.base.content.itemadapter.Item
import com.vodovoz.app.ui.model.BannerUI

data class HomeBanners(
    val id : Int,
    val items: List<BannerUI>,
    val bannerRatio: Double
) : Item {

    override fun getItemViewType(): Int {
        return if (bannerRatio == 0.41) {
            BANNER_SMALL
        } else {
            BANNER_LARGE
        }
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeBanners) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomeBanners) return false

        return this == item
    }

    companion object {
        const val BANNER_SMALL = 41
        const val BANNER_LARGE = 50
    }
}