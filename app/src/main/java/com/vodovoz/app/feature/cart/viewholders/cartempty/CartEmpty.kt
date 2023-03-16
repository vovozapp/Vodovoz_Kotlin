package com.vodovoz.app.feature.cart.viewholders.cartempty

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class CartEmpty(
    val id: Int
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.item_cart_empty
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is CartEmpty) return false

        return id == item.id
    }

}
