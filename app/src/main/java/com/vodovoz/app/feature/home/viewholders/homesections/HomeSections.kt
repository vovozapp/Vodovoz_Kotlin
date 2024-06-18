package com.vodovoz.app.feature.home.viewholders.homesections

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.SectionsUI

data class HomeSections(
    val id: Int,
    val items: SectionsUI,
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_slider_sections
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeSections) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomeSections) return false

        return this == item
    }
}