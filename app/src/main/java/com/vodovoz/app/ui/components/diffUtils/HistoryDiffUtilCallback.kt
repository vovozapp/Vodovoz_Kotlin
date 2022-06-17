package com.vodovoz.app.ui.components.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.HistoryUI

class HistoryDiffUtilCallback(
    private val oldList: List<HistoryUI>,
    private val newList: List<HistoryUI>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        if (oldItem.detailPicture != newItem.detailPicture) return false

        return true
    }
}