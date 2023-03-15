package com.vodovoz.app.feature.home.viewholders.homepopulars.inneradapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.CategoryUI

class HomePopularsInnerAdapter(
    private val clickListener: HomePopularCategoriesSliderClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            CategoryUI.CATEGORY_UI_VIEW_TYPE -> {
                HomePopularsInnerViewHolder(getViewFromInflater(R.layout.view_holder_slider_popular_category, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}