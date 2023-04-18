package com.vodovoz.app.ui.model

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class HowToOrderStepUI(
    val stepTitle: String,
    val stepDetails: String,
    val stepImageResId: Int
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.pager_how_to_order
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HowToOrderStepUI) return false

        return this == item
    }

}