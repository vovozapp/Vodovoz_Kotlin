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
        return COMMENT_VIEW_TYPE
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is CommentUI) return false

        return id == item.id
    }

    companion object {
        const val COMMENT_VIEW_TYPE = -555
    }
}