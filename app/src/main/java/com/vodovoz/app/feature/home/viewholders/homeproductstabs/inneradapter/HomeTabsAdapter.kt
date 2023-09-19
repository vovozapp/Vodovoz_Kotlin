package com.vodovoz.app.feature.home.viewholders.homeproductstabs.inneradapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.home.viewholders.homeproductstabs.HomeTabsClickListener

class HomeTabsAdapter(
    private val clickListener: HomeTabsClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            R.layout.view_holder_slider_product_category -> {
                HomeTabsViewHolder(getViewFromInflater(R.layout.view_holder_slider_popular_category, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}