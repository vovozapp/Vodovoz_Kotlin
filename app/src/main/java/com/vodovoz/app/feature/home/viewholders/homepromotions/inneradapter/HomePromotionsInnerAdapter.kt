package com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.ui.model.PromotionUI.Companion.PROMOTION_UI_VIEW_TYPE

class HomePromotionsInnerAdapter(
    private val clickListener: ProductsClickListener,
    private val promotionsClickListener: PromotionsClickListener,
    private val cartManager: CartManager,
    private val likeManager: LikeManager
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            PROMOTION_UI_VIEW_TYPE -> {
                HomePromotionsInnerViewHolder(getViewFromInflater(R.layout.view_holder_slider_promotion, parent), clickListener = clickListener, promotionsClickListener = promotionsClickListener, cartManager, likeManager)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}