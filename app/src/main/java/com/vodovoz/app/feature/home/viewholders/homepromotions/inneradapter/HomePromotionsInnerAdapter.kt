package com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder

class HomePromotionsInnerAdapter(
    private val clickListener: HomePromotionsSliderClickListener,
    private val cartManager: CartManager
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            R.layout.view_holder_slider_promotion -> {
                HomePromotionsInnerViewHolder(getViewFromInflater(viewType, parent), clickListener, cartManager)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}