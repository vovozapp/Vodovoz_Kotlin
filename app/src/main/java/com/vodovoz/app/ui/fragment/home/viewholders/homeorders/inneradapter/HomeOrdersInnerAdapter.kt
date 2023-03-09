package com.vodovoz.app.ui.fragment.home.viewholders.homeorders.inneradapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.ui.base.content.itemadapter.Item
import com.vodovoz.app.ui.base.content.itemadapter.ItemAdapter
import com.vodovoz.app.ui.base.content.itemadapter.ItemViewHolder

class HomeOrdersInnerAdapter(
    private val clickListener: HomeOrdersSliderClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            R.layout.view_holder_slider_order -> {
                HomeOrdersInnerViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}