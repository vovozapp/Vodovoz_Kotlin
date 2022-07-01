package com.vodovoz.app.ui.components.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.OrderUI

class OrderDiffUtilCallback(
    private val oldList: List<OrderUI>,
    private val newList: List<OrderUI>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        if (oldItem.status != newItem.status) return false
        if (oldItem.address != newItem.address) return false
        if (oldItem.price != newItem.price) return false

        return true
    }
}