package com.vodovoz.app.ui.fragment.home.viewholders.homecomments.inneradapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.ui.base.content.itemadapter.Item
import com.vodovoz.app.ui.base.content.itemadapter.ItemAdapter
import com.vodovoz.app.ui.base.content.itemadapter.ItemViewHolder

class HomeCommentsInnerAdapter(
    private val clickListener: HomeCommentsSliderClickListener
) : ItemAdapter(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when(viewType) {
            R.layout.view_holder_slider_comment -> {
                HomeCommentsInnerViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}