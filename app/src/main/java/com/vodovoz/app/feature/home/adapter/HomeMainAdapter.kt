package com.vodovoz.app.feature.home.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.home.viewholders.homebanners.HomeBanners.Companion.BANNER_LARGE
import com.vodovoz.app.feature.home.viewholders.homebanners.HomeBanners.Companion.BANNER_SMALL
import com.vodovoz.app.feature.home.viewholders.homebanners.HomeBannersSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homebottominfo.HomeBottomInfoViewHolder
import com.vodovoz.app.feature.home.viewholders.homebrands.HomeBrandsSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homecomments.HomeCommentsSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homecountries.HomeCountriesSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homehistories.HomeHistoriesSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homeorders.HomeOrdersSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homepopulars.HomePopularCategoriesSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProductsSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homeproductstabs.HomeProductsTabsViewHolder
import com.vodovoz.app.feature.home.viewholders.homeproductstabs.HomeTabsClickListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.HomePromotionsSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitleClickListener
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitleViewHolder
import com.vodovoz.app.feature.home.viewholders.hometriplenav.HomeTripleNavViewHolder

class HomeMainAdapter(
    private val clickListener: HomeMainClickListener,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val productsShowAllListener: ProductsShowAllListener,
    private val productsClickListener: ProductsClickListener,
    private val promotionsClickListener: PromotionsClickListener,
    private val homeTitleClickListener: HomeTitleClickListener,
    private val homeTabsClickListener: HomeTabsClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when (viewType) {
            BANNER_SMALL -> {
                HomeBannersSliderViewHolder(getViewFromInflater(R.layout.fragment_slider_banner, parent), clickListener, parent.width, 0.48)
            }
            BANNER_LARGE -> {
                HomeBannersSliderViewHolder(getViewFromInflater(R.layout.fragment_slider_banner, parent), clickListener, parent.width, 0.52)
            }
            R.layout.fragment_section_additional_info -> {
                HomeBottomInfoViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.fragment_slider_brand -> {
                HomeBrandsSliderViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.fragment_slider_comment -> {
                HomeCommentsSliderViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.fragment_slider_country -> {
                HomeCountriesSliderViewHolder(getViewFromInflater(viewType, parent), clickListener, parent.width)
            }
            R.layout.fragment_slider_history -> {
                HomeHistoriesSliderViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.fragment_slider_order -> {
                HomeOrdersSliderViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.fragment_slider_popular -> {
                HomePopularCategoriesSliderViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.fragment_slider_product -> {
                HomeProductsSliderViewHolder(getViewFromInflater(viewType, parent), productsShowAllListener, productsClickListener, cartManager, likeManager)
            }
            R.layout.fragment_slider_promotion -> {
                HomePromotionsSliderViewHolder(getViewFromInflater(viewType, parent), cartManager, likeManager, promotionsClickListener, productsClickListener)
            }
            R.layout.fragment_triple_navigation_home -> {
                HomeTripleNavViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.view_holder_flow_title -> {
                HomeTitleViewHolder(getViewFromInflater(viewType, parent), homeTitleClickListener)
            }
            R.layout.view_holder_products_tabs -> {
                HomeProductsTabsViewHolder(getViewFromInflater(viewType, parent), homeTabsClickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}