package com.vodovoz.app.ui.fragment.home.viewholders.homebottominfo

import com.vodovoz.app.R
import com.vodovoz.app.ui.fragment.home.adapter.Item
import com.vodovoz.app.ui.model.BannerUI

data class HomeBottomInfo(
    val id : Int
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_section_additional_info
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeBottomInfo) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomeBottomInfo) return false

        return this == item
    }
}
