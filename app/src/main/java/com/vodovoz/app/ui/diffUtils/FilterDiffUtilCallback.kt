package com.vodovoz.app.ui.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.FilterValueUI

class FilterDiffUtilCallback(
    private val oldList: List<FilterUI>,
    private val newList: List<FilterUI>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ) = oldList[oldItemPosition].name == newList[newItemPosition].name

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ) : Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem.code != newItem.code) return false
        if (!areFilterValueListTheSame(oldItem.filterValueList, newItem.filterValueList)) return false

        return true
    }

    private fun areFilterValueListTheSame(
        oldFilterValueList: List<FilterValueUI>,
        newFilterValueList: List<FilterValueUI>
    ) : Boolean {
        if (oldFilterValueList.size != newFilterValueList.size) return false

        for (index in oldFilterValueList.indices) {
            val oldFilter = oldFilterValueList[index]
            val newFilter = newFilterValueList[index]

            if (oldFilter.id != newFilter.id) return false
            if (oldFilter.value != newFilter.value) return false
            if (oldFilter.isSelected != newFilter.isSelected) return false
        }

        return true
    }

}