package com.vodovoz.app.feature.productdetail.viewholders.detailslisttitles

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class DetailsTitle(
    val id: Int,
    val name: String,
    val categoryProductsName: String = "",
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_flow_details_title
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailsTitle) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is DetailsTitle) return false

        return this == item
    }
}