package com.vodovoz.app.ui.fragment.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.HomeBanners
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.HomeBannersSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homebottominfo.HomeBottomInfo
import com.vodovoz.app.ui.fragment.home.viewholders.homebottominfo.HomeBottomInfoViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homebrands.HomeBrandsSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homecomments.HomeCommentsSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homecountries.HomeCountriesSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homehistories.HomeHistoriesSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homeorders.HomeOrdersSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homepopulars.HomePopularCategoriesSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.HomeProductsSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.HomePromotionsSliderViewHolder
import com.vodovoz.app.ui.fragment.home.viewholders.hometriplenav.HomeTripleNavViewHolder

class HomeMainAdapter(
    private val clickListener: HomeMainClickListener
) : ListAdapter<Item, RecyclerView.ViewHolder>(HomeMainDiffUtilCallback()) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getItemViewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            R.layout.fragment_slider_banner -> {
                HomeBannersSliderViewHolder(getViewFromInflater(viewType, parent), clickListener)
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
                HomeCountriesSliderViewHolder(getViewFromInflater(viewType, parent), clickListener)
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
                holder.bind(getItem(position) as HomeBanners)
            }
            is HomeBottomInfoViewHolder -> {
                holder.bind()
            }

        }
    }
}