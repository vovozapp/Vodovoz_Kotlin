package com.vodovoz.app.feature.product_comments

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderCommentsHeaderBinding
import com.vodovoz.app.ui.model.CommentsDataUI

class CommentsHeaderViewHolder(
    view: View,
) : ItemViewHolder<CommentsDataUI>(view) {

    private val binding: ViewHolderCommentsHeaderBinding =
        ViewHolderCommentsHeaderBinding.bind(view)

    override fun bind(item: CommentsDataUI) {
        super.bind(item)

        binding.ratingText.text = item.rating
        binding.counterComments.text = item.commentCountText
    }
}