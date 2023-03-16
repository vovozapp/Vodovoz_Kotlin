package com.vodovoz.app.feature.cart.viewholders.cartnotavailableproducts

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.ProductUI

data class CartNotAvailableProducts(
    val id: Int,
    val items: List<ProductUI>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.item_cart_not_available_products
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is CartNotAvailableProducts) return false

        return id == item.id
    }

}