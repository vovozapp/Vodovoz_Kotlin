package com.vodovoz.app.feature.productdetail.viewholders.detailservices

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class DetailServices(
    val id: Int
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_product_details_services
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailServices) return false

        return id == item.id
    }
}
