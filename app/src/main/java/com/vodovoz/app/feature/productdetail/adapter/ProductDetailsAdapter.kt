package com.vodovoz.app.feature.productdetail.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener

class ProductDetailsAdapter(
    private val clickListener: ProductDetailsClickListener,
    private val productsClickListener: ProductsClickListener,
    private val cartManager: CartManager,
    private val likeManager: LikeManager
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {

            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}