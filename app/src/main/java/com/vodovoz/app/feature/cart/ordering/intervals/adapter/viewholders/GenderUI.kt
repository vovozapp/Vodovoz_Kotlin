package com.vodovoz.app.feature.cart.ordering.intervals.adapter.viewholders

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class GenderUI(
    val name: String,
    val isSelected: Boolean = false
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.view_holder_gender
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is GenderUI) return false

        return this == item
    }

}