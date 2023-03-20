package com.vodovoz.app.feature.productdetail.viewholders.detailcomments

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class DetailComments(
    val id: Int
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_product_details_comments
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailComments) return false

        return id == item.id
    }
}