package com.vodovoz.app.feature.home.viewholders.homepromotions

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI

data class HomePromotions(
    val id: Int,
    val items: PromotionsSliderBundleUI,
    val whiteBg: Boolean = false,
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_slider_promotion
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomePromotions) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomePromotions) return false

        return this == item
    }
}
