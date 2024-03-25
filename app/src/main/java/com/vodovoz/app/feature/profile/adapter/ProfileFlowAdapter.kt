package com.vodovoz.app.feature.profile.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter.HomeOrdersSliderClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProductsSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitleViewHolder
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.profile.viewholders.ProfileBestForYouViewHolder
import com.vodovoz.app.feature.profile.viewholders.ProfileBlockViewHolder
import com.vodovoz.app.feature.profile.viewholders.ProfileHeaderViewHolder
import com.vodovoz.app.feature.profile.viewholders.ProfileLogoutViewHolder
import com.vodovoz.app.feature.profile.viewholders.ProfileMainViewHolder
import com.vodovoz.app.feature.profile.viewholders.ProfileOrderSliderViewHolder
import com.vodovoz.app.util.extensions.debugLog

class ProfileFlowAdapter(
    private val clickListener: ProfileFlowClickListener,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val productsShowAllListener: ProductsShowAllListener,
    private val productsClickListener: ProductsClickListener,
    private val homeOrdersSliderClickListener: HomeOrdersSliderClickListener,
    private val repeatOrderClickListener: (Long) -> Unit,
    private val onRecyclerReady: () -> Unit
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        debugLog { "OnCreateViewHolder viewType: $viewType" }

        return when (viewType) {
            R.layout.fragment_slider_product -> {
                HomeProductsSliderViewHolder(
                    getViewFromInflater(viewType, parent),
                    productsShowAllListener,
                    productsClickListener,
                    cartManager,
                    likeManager
                )
            }

            R.layout.item_profile_header -> {
                ProfileHeaderViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }

            R.layout.item_profile_order_slider -> {
                ProfileOrderSliderViewHolder(
                    getViewFromInflater(viewType, parent),
                    homeOrdersSliderClickListener,
                    repeatOrderClickListener
                )
            }

            R.layout.item_profile_logout -> {
                ProfileLogoutViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }

            R.layout.item_profile_best_for_you -> {
                ProfileBestForYouViewHolder(
                    getViewFromInflater(viewType, parent),
                    cartManager,
                    likeManager,
                    ratingProductManager,
                    productsClickListener,
                            onRecyclerReady
                )
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

            R.layout.item_progress -> {
                BottomProgressViewHolder(getViewFromInflater(viewType, parent))
            }

            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}