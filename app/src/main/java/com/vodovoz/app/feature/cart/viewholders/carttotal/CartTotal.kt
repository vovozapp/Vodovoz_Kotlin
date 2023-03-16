package com.vodovoz.app.feature.cart.viewholders.carttotal

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.util.CalculatedPrices

data class CartTotal(
    val id: Int,
    val prices: CalculatedPrices
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.item_cart_total
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is CartTotal) return false

        return id == item.id
    }

}