package com.vodovoz.app.feature.productdetail.viewholders.detailcomments

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.all.comments.model.CommentImage
import com.vodovoz.app.ui.model.CommentUI

data class DetailComments(
    val id: Int,
    val commentUIList: List<CommentUI>,
    val productId: Long,
    val commentImages: List<CommentImage>? = null
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_product_details_comments
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailComments) return false

        return id == item.id
    }
}