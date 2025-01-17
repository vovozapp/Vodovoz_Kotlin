package com.vodovoz.app.feature.cart.viewholders.cartnotavailableproducts.inner

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.model.ProductUI

class NotAvailableProductsAdapter(
    private val clickListener: CartMainClickListener,
    private val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            ProductUI.PRODUCT_VIEW_TYPE -> {
                NotAvailableProductsViewHolder(getViewFromInflater(R.layout.view_holder_product_list_not_available, parent), clickListener, productsClickListener, likeManager)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}