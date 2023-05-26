package com.vodovoz.app.feature.all.comments.model

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class CommentImage(
    val id: Long,
    val url: String
): Item {

    override fun getItemViewType(): Int {
        return R.layout.item_comment_image
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is CommentImage) return false

        return id == item.id
    }

}