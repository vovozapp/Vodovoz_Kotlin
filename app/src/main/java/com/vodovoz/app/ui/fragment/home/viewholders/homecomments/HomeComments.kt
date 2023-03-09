package com.vodovoz.app.ui.fragment.home.viewholders.homecomments

import com.vodovoz.app.R
import com.vodovoz.app.ui.base.content.itemadapter.Item
import com.vodovoz.app.ui.model.CommentUI

data class HomeComments(
    val id: Int,
    val items: List<CommentUI>
): Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_slider_comment
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HomeComments) return false

        return id == item.id
    }

    override fun areContentsTheSame(item: Item): Boolean {
        if (item !is HomeComments) return false

        return this == item
    }

}
