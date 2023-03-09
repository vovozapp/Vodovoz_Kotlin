package com.vodovoz.app.ui.fragment.home.viewholders.homecountries

import com.vodovoz.app.R
import com.vodovoz.app.ui.base.content.itemadapter.Item
import com.vodovoz.app.ui.model.custom.CountriesSliderBundleUI

data class HomeCountries(
    val id : Int,
    val items: CountriesSliderBundleUI
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_slider_country
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeCountries) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomeCountries) return false

        return this == item
    }
}