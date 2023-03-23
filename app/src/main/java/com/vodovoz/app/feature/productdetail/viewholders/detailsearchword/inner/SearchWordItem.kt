package com.vodovoz.app.feature.productdetail.viewholders.detailsearchword.inner

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class SearchWordItem(
    val query: String
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_slider_search_word
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is SearchWordItem) return false

        return this == item
    }

}