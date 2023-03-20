package com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter.inneradapterproducts

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.ProductUI

class HomePromotionsProductInnerAdapter(
    private val clickListener: HomePromotionsProductInnerClickListener,
    private val cartManager: CartManager
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            ProductUI.PRODUCT_VIEW_TYPE -> {
                HomePromotionsProductInnerViewHolder(getViewFromInflater(R.layout.view_holder_slider_promotion_product, parent), clickListener, cartManager)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}