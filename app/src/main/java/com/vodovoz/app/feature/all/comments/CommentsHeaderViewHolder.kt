package com.vodovoz.app.feature.all.comments

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderCommentWithAvatarBinding
import com.vodovoz.app.databinding.ViewHolderCommentsHeaderBinding
import com.vodovoz.app.feature.all.comments.adapter.CommentImagesAdapter
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.ui.model.CommentsDataUI
import com.vodovoz.app.util.extensions.fromHtml

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