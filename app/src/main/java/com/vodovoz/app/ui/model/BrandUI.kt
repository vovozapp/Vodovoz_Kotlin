package com.vodovoz.app.ui.model

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class BrandUI(
    val id: Long,
    val name: String,
    val detailPicture: String
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_slider_brand
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is BrandUI) return false

        return id == item.id
    }
}