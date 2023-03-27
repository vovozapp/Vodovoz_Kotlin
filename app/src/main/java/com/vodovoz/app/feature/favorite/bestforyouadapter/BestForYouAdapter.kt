package com.vodovoz.app.feature.favorite.bestforyouadapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProductsSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener

class BestForYouAdapter(
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val productsClickListener: ProductsClickListener,
    private val productsShowAllListener: ProductsShowAllListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when (viewType) {
            R.layout.fragment_slider_product -> {
                HomeProductsSliderViewHolder(getViewFromInflater(viewType, parent), productsShowAllListener, productsClickListener, cartManager, likeManager)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}