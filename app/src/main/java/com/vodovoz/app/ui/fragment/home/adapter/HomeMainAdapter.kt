package com.vodovoz.app.ui.fragment.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.HomeBanners
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.HomeBanners.Companion.BANNER_LARGE
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.HomeBanners.Companion.BANNER_SMALL
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.HomeBannersSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.inneradapter.HomeBannersInnerDiffUtilCallback
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.inneradapter.HomeBannersInnerViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homebottominfo.HomeBottomInfo
import com.vodovoz.app.ui.fragment.home.viewholders.homebottominfo.HomeBottomInfoViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homebrands.HomeBrands
import com.vodovoz.app.ui.fragment.home.viewholders.homebrands.HomeBrandsSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homecomments.HomeComments
import com.vodovoz.app.ui.fragment.home.viewholders.homecomments.HomeCommentsSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homecountries.HomeCountries
import com.vodovoz.app.ui.fragment.home.viewholders.homecountries.HomeCountriesSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homehistories.HomeHistories
import com.vodovoz.app.ui.fragment.home.viewholders.homehistories.HomeHistoriesSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homeorders.HomeOrders
import com.vodovoz.app.ui.fragment.home.viewholders.homeorders.HomeOrdersSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homepopulars.HomePopularCategoriesSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homepopulars.HomePopulars
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.HomeProductsSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.HomePromotions
import com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.HomePromotionsSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.hometriplenav.HomeTripleNavViewHolder
import com.vodovoz.app.ui.model.BannerUI

class HomeMainAdapter(
    private val clickListener: HomeMainClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Item>()

    private val adapterListUpdateCallback = AdapterListUpdateCallback(this)


    fun submitList(items: List<Item>) {
        val diff = DiffUtil.calculateDiff(HomeMainDiffUtilCallback(this.items, items), false)
        diff.dispatchUpdatesTo(adapterListUpdateCallback)

        this.items.clear()
        this.items.addAll(items)
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].getItemViewType()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int) : Item? {
        return items.getOrNull(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

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
                HomeProductsSliderViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.fragment_slider_promotion -> {
                HomePromotionsSliderViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.fragment_triple_navigation_home -> {
                HomeTripleNavViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

    private fun getViewFromInflater(layoutId: Int, parent: ViewGroup) : View {
        return LayoutInflater
            .from(parent.context)
            .inflate(layoutId, parent, false)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HomeBannersSliderViewHolder -> {
                holder.bind(items[position] as HomeBanners)
            }
            is HomeBottomInfoViewHolder -> {
                holder.bind()
            }
            is HomeBrandsSliderViewHolder -> {
                holder.bind(items[position]  as HomeBrands)
            }
            is HomeCommentsSliderViewHolder -> {
                holder.bind(items[position]  as HomeComments)
            }
            is HomeCountriesSliderViewHolder -> {
                holder.bind(items[position]  as HomeCountries)
            }
            is HomeHistoriesSliderViewHolder -> {
                holder.bind(items[position]  as HomeHistories)
            }
            is HomeOrdersSliderViewHolder -> {
                holder.bind(items[position]  as HomeOrders)
            }
            is HomePopularCategoriesSliderViewHolder -> {
                holder.bind(items[position]  as HomePopulars)
            }
            is HomeProductsSliderViewHolder -> {
                holder.bind(items[position]  as HomeProducts)
            }
            is HomePromotionsSliderViewHolder -> {
                holder.bind(items[position]  as HomePromotions)
            }
            is HomeTripleNavViewHolder -> {
                holder.bind()
            }
        }
    }
}