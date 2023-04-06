package com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.OrderUI.Companion.ORDER_VIEW_TYPE

class HomeOrdersInnerAdapter(
    private val clickListener: HomeOrdersSliderClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            ORDER_VIEW_TYPE-> {
                HomeOrdersInnerViewHolder(getViewFromInflater(R.layout.view_holder_slider_order, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}