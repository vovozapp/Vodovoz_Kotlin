package com.vodovoz.app.ui.model

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class CommentUI(
    val id: Long? = null,
    val text: String? = null,
    val author: String? = null,
    val authorPhoto: String? = null,
    val date: String? = null,
    val rating: Int? = null
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_slider_comment
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is CommentUI) return false

        return id == item.id
    }
}