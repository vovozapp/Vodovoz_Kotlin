package com.vodovoz.app.ui.components.adapter.commentWithAvatarAdapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderCommentWithAvatarBinding
import com.vodovoz.app.ui.model.CommentUI

class CommentWithAvatarViewHolder(
    private val binding: ViewHolderCommentWithAvatarBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(commentUI: CommentUI) {
        binding.date.text = commentUI.date
        binding.author.text = commentUI.author
        binding.comment.text = commentUI.text

        Glide.with(context)
            .load(commentUI.authorPhoto)
            .placeholder(ContextCompat.getDrawable(context, R.drawable.default_avatar))
            .into(binding.authorPhoto)
    }

}