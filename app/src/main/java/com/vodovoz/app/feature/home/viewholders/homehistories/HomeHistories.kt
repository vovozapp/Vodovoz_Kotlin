package com.vodovoz.app.feature.home.viewholders.homehistories

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.HistoryUI

data class HomeHistories(
    val id : Int,
    val items: List<HistoryUI>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_slider_history
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeHistories) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomeHistories) return false

        return this == item
    }
}
