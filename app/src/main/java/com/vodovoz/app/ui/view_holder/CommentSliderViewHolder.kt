package com.vodovoz.app.ui.view_holder

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

        binding.rbRating.rating = commentUI.rating!!.toFloat()
        binding.tvAuthor.text = commentUI.author
        binding.tvComment.text = commentUI.text
        binding.tvDate.text = commentUI.date
    }

}