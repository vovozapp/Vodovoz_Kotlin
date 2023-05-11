package com.vodovoz.app.ui.model.custom

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class DiscountCardPropertyUI(
    val id: Long = 0,
    val name: String = "",
    val code: String = "",
    var value: String = "",
    var isValid: Boolean = true
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_discount_card_property
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DiscountCardPropertyUI) return false

        return id == item.id
    }

}