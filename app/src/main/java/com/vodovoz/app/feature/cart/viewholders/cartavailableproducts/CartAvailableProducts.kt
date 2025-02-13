package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.parser.response.cart.MessageTextBasket
import com.vodovoz.app.ui.model.ProductUI

data class CartAvailableProducts(
    val id: Int,
    val items: List<ProductUI>,
    val showCheckForm: Boolean = false,
    val showReturnBottleBtn: Boolean = false,
    val giftMessage: MessageTextBasket? = null
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.item_cart_available_products
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is CartAvailableProducts) return false

        return id == item.id
    }

}
