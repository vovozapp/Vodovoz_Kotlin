package com.vodovoz.app.ui.components.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.CommentUI

class CommentSliderDiffUtilCallback(
    private val oldList: List<CommentUI>,
    private val newList: List<CommentUI>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        if (oldItem.date != newItem.date) return false
        if (oldItem.text != newItem.text) return false
        if (oldItem.author != newItem.author) return false

        return true
    }
}