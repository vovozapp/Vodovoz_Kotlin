package com.vodovoz.app.ui.fragment.home.viewholders.homeproducts

import com.vodovoz.app.R
import com.vodovoz.app.ui.fragment.home.adapter.Item
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.CategoryUI

data class HomeProducts(
    val id : Int,
    val items: List<CategoryDetailUI>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_slider_product
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeProducts) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomeProducts) return false

        return this == item
    }
}