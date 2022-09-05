package com.vodovoz.app.ui.view_holder

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
        binding.tvAuthor.text = commentUI.author
        binding.tvComment.text = commentUI.text

        Glide.with(context)
            .load(commentUI.authorPhoto)
            .placeholder(ContextCompat.getDrawable(context, R.drawable.png_default_avatar))
            .into(binding.imgAuthorPhoto)
    }

}