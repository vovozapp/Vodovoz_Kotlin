package com.vodovoz.app.feature.productdetail.viewholders.detailtabs

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class DetailTabs(
    val id: Int
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_product_details_tabs
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailTabs) return false

        return id == item.id
    }
}
