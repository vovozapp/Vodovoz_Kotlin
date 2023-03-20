package com.vodovoz.app.feature.productdetail.viewholders.detailheader

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class DetailHeader(
    val id: Int
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_product_details_header
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailHeader) return false

        return id == item.id
    }
}
