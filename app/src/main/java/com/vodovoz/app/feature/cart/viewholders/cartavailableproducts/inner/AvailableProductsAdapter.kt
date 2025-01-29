package com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.productlist.viewholders.ProductsGridViewHolder
import com.vodovoz.app.ui.model.ProductUI.Companion.PRODUCT_VIEW_TYPE
import com.vodovoz.app.ui.model.ProductUI.Companion.PRODUCT_VIEW_TYPE_GRID

class AvailableProductsAdapter(
    private val productsClickListener: ProductsClickListener,
    private val likeManager: LikeManager,
    private val cartManager: CartManager,
    private val ratingProductManager: RatingProductManager
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            PRODUCT_VIEW_TYPE -> {
                AvailableProductsViewHolder(getViewFromInflater(R.layout.view_holder_product_cart, parent), productsClickListener, likeManager, cartManager, ratingProductManager)
            }
            PRODUCT_VIEW_TYPE_GRID -> {
                ProductsGridViewHolder(getViewFromInflater(R.layout.view_holder_product_grid, parent), productsClickListener, likeManager, cartManager, ratingProductManager)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}