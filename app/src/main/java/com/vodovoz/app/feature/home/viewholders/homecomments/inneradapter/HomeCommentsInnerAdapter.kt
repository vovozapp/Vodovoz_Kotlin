package com.vodovoz.app.feature.home.viewholders.homecomments.inneradapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.CommentUI.Companion.COMMENT_VIEW_TYPE

class HomeCommentsInnerAdapter(
    private val clickListener: HomeCommentsSliderClickListener
) : ItemAdapter(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when(viewType) {
            COMMENT_VIEW_TYPE -> {
                HomeCommentsInnerViewHolder(getViewFromInflater(R.layout.view_holder_slider_comment, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}