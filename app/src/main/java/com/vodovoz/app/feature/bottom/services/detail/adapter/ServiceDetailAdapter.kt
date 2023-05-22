package com.vodovoz.app.feature.bottom.services.detail.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter.inneradapterproducts.HomePromotionsProductInnerViewHolder
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.productlist.viewholders.ProdListViewHolder
import com.vodovoz.app.ui.model.ProductUI

class ServiceDetailAdapter(
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val productsClickListener: ProductsClickListener
): ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            R.layout.item_service_detail -> {
                ServiceDetailViewHolder(getViewFromInflater(viewType, parent), cartManager, likeManager, ratingProductManager, productsClickListener)
            }
            ProductUI.PRODUCT_VIEW_TYPE -> {
                ProdListViewHolder(getViewFromInflater(R.layout.view_holder_product_list, parent), productsClickListener, likeManager, cartManager, ratingProductManager)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}