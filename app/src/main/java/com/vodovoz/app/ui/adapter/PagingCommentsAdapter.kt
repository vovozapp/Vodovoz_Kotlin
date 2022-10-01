package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.vodovoz.app.databinding.ViewHolderCommentWithAvatarBinding
import com.vodovoz.app.ui.diffUtils.CommentDiffItemCallback
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.ui.view_holder.CommentWithAvatarViewHolder

class PagingCommentsAdapter(
    private val commentDiffItemCallback: CommentDiffItemCallback
) : PagingDataAdapter<CommentUI, CommentWithAvatarViewHolder>(commentDiffItemCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CommentWithAvatarViewHolder(
        binding = ViewHolderCommentWithAvatarBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: CommentWithAvatarViewHolder,
        position: Int
    ) = holder.onBind(getItem(position)!!)

    enum class ViewMode(val code: Int) {
        LIST(0), GRID(1)
    }

}