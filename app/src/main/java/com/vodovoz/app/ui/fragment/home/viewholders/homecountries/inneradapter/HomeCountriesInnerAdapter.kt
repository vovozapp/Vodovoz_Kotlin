package com.vodovoz.app.ui.fragment.home.viewholders.homecountries.inneradapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.ui.base.content.itemadapter.Item
import com.vodovoz.app.ui.base.content.itemadapter.ItemAdapter
import com.vodovoz.app.ui.base.content.itemadapter.ItemViewHolder

class HomeCountriesInnerAdapter(
    private val clickListener: HomeCountriesSliderClickListener,
    private val cardWidth: Double
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            R.layout.view_holder_slider_country -> {
                HomeCountriesInnerViewHolder(getViewFromInflater(viewType, parent), clickListener, cardWidth)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}