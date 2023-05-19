package com.vodovoz.app.feature.profile.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter.HomeOrdersSliderClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProductsSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitleClickListener
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitleViewHolder
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.profile.viewholders.*

class ProfileFlowAdapter(
    private val clickListener: ProfileFlowClickListener,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
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
            R.layout.item_profile_order_slider -> {
                ProfileOrderSliderViewHolder(getViewFromInflater(viewType, parent), homeOrdersSliderClickListener)
            }
            R.layout.item_profile_logout -> {
                ProfileLogoutViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.item_profile_best_for_you -> {
                ProfileBestForYouViewHolder(getViewFromInflater(viewType, parent), cartManager, likeManager, ratingProductManager, productsClickListener)
            }
            R.layout.item_profile_main_rv -> {
                ProfileMainViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.item_profile_block -> {
                ProfileBlockViewHolder(getViewFromInflater(viewType, parent))
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