package com.vodovoz.app.feature.bottom.services.detail.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener

class ServiceDetailAdapter(
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val productsClickListener: ProductsClickListener,
    private val clickListener: ServiceDetailClickListener
): ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            R.layout.item_service_detail -> {
                ServiceDetailViewHolder(getViewFromInflater(viewType, parent), cartManager, likeManager, productsClickListener, clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}