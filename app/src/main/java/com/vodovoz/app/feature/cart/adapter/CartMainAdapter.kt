package com.vodovoz.app.feature.cart.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.CartAvailableProductsViewHolder
import com.vodovoz.app.feature.cart.viewholders.cartempty.CartEmptyViewHolder
import com.vodovoz.app.feature.cart.viewholders.cartnotavailableproducts.CartNotAvailableProductsViewHolder
import com.vodovoz.app.feature.cart.viewholders.carttotal.CartTotalViewHolder
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.HomeCategoriesInnerViewHolder
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitleViewHolder
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener

class CartMainAdapter(
    private val clickListener: CartMainClickListener,
    private val productsClickListener: ProductsClickListener,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            R.layout.item_cart_available_products -> {
                CartAvailableProductsViewHolder(getViewFromInflater(viewType, parent), clickListener, productsClickListener, likeManager, cartManager, ratingProductManager)
            }
            R.layout.item_cart_not_available_products -> {
                CartNotAvailableProductsViewHolder(getViewFromInflater(viewType, parent), clickListener, productsClickListener, likeManager)
            }
            R.layout.item_cart_total -> {
                CartTotalViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.view_holder_slider_product_category -> {
                HomeCategoriesInnerViewHolder(getViewFromInflater(viewType, parent), productsClickListener, cartManager, likeManager)
            }
            R.layout.item_cart_empty -> {
                CartEmptyViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.view_holder_flow_title -> {
                HomeTitleViewHolder(getViewFromInflater(viewType, parent))
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}