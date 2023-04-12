package com.vodovoz.app.feature.profile.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.home.viewholders.homeorders.HomeOrdersSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter.HomeOrdersSliderClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProductsSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.profile.viewholders.ProfileHeaderViewHolder
import com.vodovoz.app.feature.profile.viewholders.ProfileOrderSliderViewHolder

class ProfileFlowAdapter(
    private val clickListener: ProfileFlowClickListener,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val productsShowAllListener: ProductsShowAllListener,
    private val productsClickListener: ProductsClickListener,
    private val homeOrdersSliderClickListener: HomeOrdersSliderClickListener
) : ItemAdapter()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when (viewType) {
            R.layout.fragment_slider_product -> {
                HomeProductsSliderViewHolder(getViewFromInflater(viewType, parent), productsShowAllListener, productsClickListener, cartManager, likeManager)
            }
            R.layout.item_profile_header -> {
                ProfileHeaderViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.fragment_slider_order -> {
                ProfileOrderSliderViewHolder(getViewFromInflater(viewType, parent), homeOrdersSliderClickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}