package com.vodovoz.app.ui.fragment.home.viewholders.homepopulars

import com.vodovoz.app.R
import com.vodovoz.app.ui.base.content.itemadapter.Item
import com.vodovoz.app.ui.model.CategoryUI

data class HomePopulars(
    val id : Int,
    val items: List<CategoryUI>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_slider_popular
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomePopulars) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomePopulars) return false

        return this == item
    }
}
