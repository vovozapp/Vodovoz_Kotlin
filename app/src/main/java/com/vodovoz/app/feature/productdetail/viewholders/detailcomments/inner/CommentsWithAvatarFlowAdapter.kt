package com.vodovoz.app.feature.productdetail.viewholders.detailcomments.inner

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressViewHolder
import com.vodovoz.app.feature.all.comments.CommentsHeaderViewHolder
import com.vodovoz.app.ui.model.CommentUI.Companion.COMMENT_VIEW_TYPE

class CommentsWithAvatarFlowAdapter : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when(viewType) {
            COMMENT_VIEW_TYPE -> {
                CommentsWithAvatarFlowViewHolder(getViewFromInflater(R.layout.view_holder_comment_with_avatar, parent))
            }

            R.layout.view_holder_comments_header -> {
                CommentsHeaderViewHolder(getViewFromInflater(R.layout.view_holder_comments_header, parent))
            }

            R.layout.item_progress -> {
                BottomProgressViewHolder(getViewFromInflater(viewType, parent))
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}