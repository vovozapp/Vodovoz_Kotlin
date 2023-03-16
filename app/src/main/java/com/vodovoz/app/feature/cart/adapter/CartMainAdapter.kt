package com.vodovoz.app.feature.cart.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.CartAvailableProductsViewHolder
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner.CartProductsViewHolder
import com.vodovoz.app.ui.model.ProductUI

class CartMainAdapter(
    private val clickListener: CartMainClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            R.layout.item_cart_available_products -> {
                CartAvailableProductsViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            ProductUI.PRODUCT_VIEW_TYPE -> {
                CartProductsViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}