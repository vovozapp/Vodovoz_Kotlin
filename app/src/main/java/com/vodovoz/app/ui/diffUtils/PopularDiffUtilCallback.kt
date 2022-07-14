package com.vodovoz.app.ui.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.CategoryUI

class PopularDiffUtilCallback(
    private val oldList: List<CategoryUI>,
    private val newList: List<CategoryUI>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        if (oldItem.name != newItem.name) return false

        return true
    }
}