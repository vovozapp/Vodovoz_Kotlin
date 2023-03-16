package com.vodovoz.app.feature.cart.viewholders.cartnotavailableproducts.inner

import android.view.ViewGroup
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.ui.model.ProductUI

class NotAvailableProductsAdapter(
    private val clickListener: CartMainClickListener,
    private val productsClickListener: ProductsClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            ProductUI.PRODUCT_VIEW_TYPE -> {
                NotAvailableProductsViewHolder(getViewFromInflater(viewType, parent), clickListener, productsClickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}