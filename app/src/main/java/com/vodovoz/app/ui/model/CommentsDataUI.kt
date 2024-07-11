package com.vodovoz.app.ui.model

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class CommentsDataUI(
    val rating: String = "0",
    val commentCount: Int = 0,
    val commentCountText: String = "Всего 0 отзывов",
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.view_holder_comments_header
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if(item !is CommentsDataUI) return false
        return this == item
    }

}
