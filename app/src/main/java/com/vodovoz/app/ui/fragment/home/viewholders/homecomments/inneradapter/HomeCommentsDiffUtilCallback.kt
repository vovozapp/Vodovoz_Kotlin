package com.vodovoz.app.ui.fragment.home.viewholders.homecomments.inneradapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.CommentUI

class HomeCommentsDiffUtilCallback: DiffUtil.ItemCallback<CommentUI>() {

    override fun areContentsTheSame(oldItem: CommentUI, newItem: CommentUI): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: CommentUI, newItem: CommentUI): Boolean {
        return oldItem.id == newItem.id
    }
}