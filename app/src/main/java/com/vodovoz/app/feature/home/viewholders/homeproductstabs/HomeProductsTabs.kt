package com.vodovoz.app.feature.home.viewholders.homeproductstabs

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.CategoryDetailUI

data class HomeProductsTabs(
    val id: Int,
    val tabsNames: List<CategoryDetailUI>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_products_tabs
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeProductsTabs) return false

        return id == item.id
    }

}
