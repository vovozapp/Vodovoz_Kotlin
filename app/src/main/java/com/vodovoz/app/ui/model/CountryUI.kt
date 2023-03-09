package com.vodovoz.app.ui.model

import com.vodovoz.app.R
import com.vodovoz.app.ui.base.content.itemadapter.Item

data class CountryUI(
    val id: Long,
    val name: String,
    val detailPicture: String
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_slider_country
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is CountryUI) return false

        return id == item.id
    }
}