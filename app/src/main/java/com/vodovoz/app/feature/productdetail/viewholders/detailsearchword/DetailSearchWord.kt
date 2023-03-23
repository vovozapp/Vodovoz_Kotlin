package com.vodovoz.app.feature.productdetail.viewholders.detailsearchword

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.productdetail.viewholders.detailsearchword.inner.SearchWordItem

data class DetailSearchWord(
    val id: Int,
    val searchWordList: List<SearchWordItem>,
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_product_details_search_word
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailSearchWord) return false

        return id == item.id
    }
}
