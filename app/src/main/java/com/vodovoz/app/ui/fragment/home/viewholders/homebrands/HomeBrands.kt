package com.vodovoz.app.ui.fragment.home.viewholders.homebrands

import com.vodovoz.app.R
import com.vodovoz.app.ui.fragment.home.adapter.Item
import com.vodovoz.app.ui.model.BrandUI

data class HomeBrands(
    val id : Int,
    val items: List<BrandUI>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_slider_brand
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeBrands) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomeBrands) return false

        return this == item
    }
}