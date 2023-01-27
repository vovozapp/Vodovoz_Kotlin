package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderCommentWithAvatarBinding
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.ui.view_holder.CommentWithAvatarViewHolder

class CommentsWithAvatarAdapter : RecyclerView.Adapter<CommentWithAvatarViewHolder>() {

    var commentUiList = listOf<CommentUI>()

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
    ) = holder.onBind(commentUiList[position])

    override fun getItemCount() = commentUiList.size
}