package com.vodovoz.app.ui.components.adapter.commentSliderAdapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderCommentBinding
import com.vodovoz.app.ui.model.CommentUI

class CommentSliderViewHolder(
    private val binding: ViewHolderSliderCommentBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var commentUI: CommentUI

    fun onBind(commentUI: CommentUI) {
        this.commentUI = commentUI

        binding.rating.rating = commentUI.rating!!.toFloat()
        binding.author.text = commentUI.author
        binding.comment.text = commentUI.text
        binding.date.text = commentUI.date
    }

}