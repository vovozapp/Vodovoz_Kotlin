package com.vodovoz.app.feature.home.viewholders.hometitle

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class HomeTitle(
    val id: Int,
    val name: String,
    val showAll: Boolean,
    val showAllName: String,
    val type: Int,
    val categoryId: Long
) : Item {

    companion object {
        const val HISTORIES_TITLE = 0
        const val POPULARS_TITLE = 1
        const val DISCOUNT_TITLE = 2
        const val ORDERS_TITLE = 3
        const val NOVELTIES_TITLE = 4
        const val PROMOTIONS_TITLE = 5
        const val BRANDS_TITLE = 6
        const val VIEWED_TITLE = 7
        const val COMMENTS_TITLE = 8
    }

    override fun getItemViewType(): Int {
        return R.layout.view_holder_flow_title
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeTitle) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomeTitle) return false

        return this == item
    }
}