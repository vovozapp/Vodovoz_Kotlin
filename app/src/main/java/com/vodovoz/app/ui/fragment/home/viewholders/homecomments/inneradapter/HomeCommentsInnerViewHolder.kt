package com.vodovoz.app.ui.fragment.home.viewholders.homecomments.inneradapter

import android.view.View
import com.vodovoz.app.databinding.ViewHolderSliderCommentBinding
import com.vodovoz.app.ui.base.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.CommentUI

class HomeCommentsInnerViewHolder(
    view: View,
    private val clickListener: HomeCommentsSliderClickListener
) : ItemViewHolder<CommentUI>(view) {

    private val binding: ViewHolderSliderCommentBinding = ViewHolderSliderCommentBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener.onCommentClick(item.id)
        }
    }

    override fun bind(item: CommentUI) {
        super.bind(item)
        binding.rbRating.rating = item.rating?.toFloat() ?: 0f
        binding.tvAuthor.text = if (item.author == "null") "" else item.author
        binding.tvComment.text = item.text ?: ""
        binding.tvDate.text = item.date ?: ""
    }

    private fun getItemByPosition(): CommentUI? {
        return (bindingAdapter as? HomeCommentsInnerAdapter)?.getItem(bindingAdapterPosition) as? CommentUI
    }
}