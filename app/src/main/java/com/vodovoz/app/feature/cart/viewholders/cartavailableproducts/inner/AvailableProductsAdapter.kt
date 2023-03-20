package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.ui.model.ProductUI.Companion.PRODUCT_VIEW_TYPE

class AvailableProductsAdapter(
    private val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            PRODUCT_VIEW_TYPE -> {
                AvailableProductsViewHolder(getViewFromInflater(R.layout.view_holder_product_list, parent), productsClickListener, likeManager)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}