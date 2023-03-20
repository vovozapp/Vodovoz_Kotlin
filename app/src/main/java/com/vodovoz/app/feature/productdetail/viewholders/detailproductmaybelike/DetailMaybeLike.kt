package com.vodovoz.app.feature.productdetail.viewholders.detailproductmaybelike

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class DetailMaybeLike(
    val id: Int
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_product_details_maybe_like_product_list
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailMaybeLike) return false

        return id == item.id
    }
}
