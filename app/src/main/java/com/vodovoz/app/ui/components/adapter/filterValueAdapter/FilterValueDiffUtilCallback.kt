package com.vodovoz.app.ui.components.adapter.filterValueAdapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.FilterValueUI

class FilterValueDiffUtilCallback(
    private val oldList: List<FilterValueUI>,
    private val newList: List<FilterValueUI>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ) = oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        if (oldList[oldItemPosition].value != newList[newItemPosition].value) return false
        if (oldList[oldItemPosition].isSelected != newList[newItemPosition].isSelected) return false

        return true
    }

}