package com.vodovoz.app.ui.components.adapter.allProductFiltersAdapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.FilterUI

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
    ) = oldList[oldItemPosition].code == newList[newItemPosition].code

}