package com.vodovoz.app.feature.productdetail.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProductsSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.HomeCategoriesInnerViewHolder
import com.vodovoz.app.feature.home.viewholders.homepromotions.HomePromotionsSliderViewHolder
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.productdetail.viewholders.detailblocks.DetailBlocksViewHolder
import com.vodovoz.app.feature.productdetail.viewholders.detailbrandproductlist.DetailBrandListViewHolder
import com.vodovoz.app.feature.productdetail.viewholders.detailcatandbrand.DetailCatAndBrandViewHolder
import com.vodovoz.app.feature.productdetail.viewholders.detailcomments.DetailCommentsViewHolder
import com.vodovoz.app.feature.productdetail.viewholders.detailheader.DetailHeaderViewHolder
import com.vodovoz.app.feature.productdetail.viewholders.detailprices.DetailPricesViewHolder
import com.vodovoz.app.feature.productdetail.viewholders.detailproductmaybelike.DetailMaybeViewHolder
import com.vodovoz.app.feature.productdetail.viewholders.detailsearchword.DetailSearchWordViewHolder
import com.vodovoz.app.feature.productdetail.viewholders.detailservices.DetailServicesViewHolder
import com.vodovoz.app.feature.productdetail.viewholders.detailslisttitles.DetailsTitleViewHolder
import com.vodovoz.app.feature.productdetail.viewholders.detailtabs.DetailTabsViewHolder
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.util.extensions.debugLog

class ProductDetailsAdapter(
    private val clickListener: ProductDetailsClickListener,
    private val productsClickListener: ProductsClickListener,
    private val productsShowAllListener: ProductsShowAllListener,
    private val promotionsClickListener: PromotionsClickListener,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {

            //Header
            R.layout.fragment_product_details_header -> {
                DetailHeaderViewHolder(
                    getViewFromInflater(viewType, parent),
                    clickListener,
                    productsClickListener,
                    likeManager,
                    cartManager
                )
            }

            //Prices
            R.layout.fragment_product_details_prices -> {
                DetailPricesViewHolder(getViewFromInflater(viewType, parent))
            }

            R.layout.fragment_product_details_blocks -> {
                DetailBlocksViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }

            //Services
            R.layout.fragment_product_details_services -> {
                DetailServicesViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }

            //Tabs
            R.layout.fragment_product_details_tabs -> {
                DetailTabsViewHolder(getViewFromInflater(viewType, parent))
            }

            //Category and Brand
            R.layout.fragment_product_details_cat_and_brand -> {
                DetailCatAndBrandViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }

            //Brand Products List
            R.layout.fragment_product_details_brand_product_list -> {
                DetailBrandListViewHolder(
                    getViewFromInflater(viewType, parent),
                    clickListener,
                    productsClickListener,
                    likeManager,
                    cartManager,
                    ratingProductManager
                )
            }

            //Products - recommendProductSliderFragment, buyWithProductsSliderFragment
            R.layout.fragment_slider_product -> {
                HomeProductsSliderViewHolder(
                    getViewFromInflater(viewType, parent),
                    productsShowAllListener,
                    productsClickListener,
                    cartManager,
                    likeManager
                )
            }

            //Promotions - promotionsSliderFragment
            R.layout.fragment_slider_promotion -> {
                HomePromotionsSliderViewHolder(
                    getViewFromInflater(viewType, parent),
                    cartManager,
                    likeManager,
                    promotionsClickListener,
                    productsClickListener
                )
            }

            //Maybe Like
            R.layout.fragment_product_details_maybe_like_product_list -> {
                DetailMaybeViewHolder(
                    getViewFromInflater(viewType, parent),
                    clickListener,
                    productsClickListener,
                    likeManager,
                    cartManager,
                    ratingProductManager
                )
            }

            //Search Word
            R.layout.fragment_product_details_search_word -> {
                DetailSearchWordViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }

            //Comments
            R.layout.fragment_product_details_comments -> {
                DetailCommentsViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }

            //Viewed
            R.layout.view_holder_slider_product_category -> {
                HomeCategoriesInnerViewHolder(
                    getViewFromInflater(viewType, parent),
                    productsClickListener,
                    cartManager,
                    likeManager
                )
            }

            //Products sliders title
            R.layout.view_holder_flow_details_title -> {
                debugLog { "view_holder_flow_title" }
                DetailsTitleViewHolder(getViewFromInflater(viewType, parent))
            }

            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}