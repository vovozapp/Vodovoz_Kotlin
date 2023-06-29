package com.vodovoz.app.ui.model

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class BrandUI(
    val id: Long,
    val name: String,
    val detailPicture: String,
    val hasList: Boolean
) : Item {

    override fun getItemViewType(): Int {
        return BRAND_UI_VIEW_TYPE
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is BrandUI) return false

        return id == item.id
    }

    companion object {
        const val BRAND_UI_VIEW_TYPE = -155252
    }
}