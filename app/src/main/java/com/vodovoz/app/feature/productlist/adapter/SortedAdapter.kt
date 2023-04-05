package com.vodovoz.app.feature.productlist.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.productlist.viewholders.ProdListViewHolder
import com.vodovoz.app.feature.productlist.viewholders.ProductsGridViewHolder
import com.vodovoz.app.ui.model.ProductUI
import javax.inject.Inject

class SortedAdapter(
    private val productsClickListener: ProductsClickListener,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager
): ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            ProductUI.PRODUCT_VIEW_TYPE -> {
                ProdListViewHolder(getViewFromInflater(R.layout.view_holder_product_list, parent), productsClickListener, likeManager, cartManager, ratingProductManager)
            }
            ProductUI.PRODUCT_VIEW_TYPE_GRID -> {
                ProductsGridViewHolder(getViewFromInflater(R.layout.view_holder_product_grid, parent), productsClickListener, likeManager, cartManager, ratingProductManager)
            }
            R.layout.item_progress -> {
                BottomProgressViewHolder(getViewFromInflater(viewType, parent))
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}