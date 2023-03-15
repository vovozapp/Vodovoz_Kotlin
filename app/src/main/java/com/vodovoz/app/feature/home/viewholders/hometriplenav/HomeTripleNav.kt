package com.vodovoz.app.feature.home.viewholders.hometriplenav

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class HomeTripleNav(
    val id : Int
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_triple_navigation_home
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeTripleNav) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomeTripleNav) return false

        return this == item
    }
}
