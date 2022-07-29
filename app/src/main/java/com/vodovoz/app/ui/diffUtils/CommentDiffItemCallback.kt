package com.vodovoz.app.ui.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.CommentUI

class CommentDiffItemCallback: DiffUtil.ItemCallback<CommentUI>() {

    override fun areItemsTheSame(oldItem: CommentUI, newItem: CommentUI): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CommentUI, newItem: CommentUI): Boolean {
        if (oldItem.text != newItem.text) return false
        if (oldItem.author != newItem.author) return false
        if (oldItem.rating != newItem.rating) return false
        if (oldItem.date != newItem.date) return false
        if (oldItem.authorPhoto != newItem.authorPhoto) return false

        return true
    }

}