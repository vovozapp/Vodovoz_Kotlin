package com.vodovoz.app.ui.fragment.home.viewholders.homecomments.inneradapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderCommentBinding
import com.vodovoz.app.ui.model.CommentUI

class HomeCommentsInnerViewHolder(
    view: View,
    private val clickListener: HomeCommentsSliderClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: ViewHolderSliderCommentBinding = ViewHolderSliderCommentBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onCommentClick(item.id)
        }
    }

    fun bind(comment: CommentUI) {
        binding.rbRating.rating = comment.rating?.toFloat() ?: 0f
        binding.tvAuthor.text = if (comment.author == "null") "" else comment.author
        binding.tvComment.text = comment.text ?: ""
        binding.tvDate.text = comment.date ?: ""
    }

    private fun getItemByPosition(): CommentUI? {
        return (bindingAdapter as? HomeCommentsInnerAdapter)?.currentList?.get(bindingAdapterPosition)
    }
}