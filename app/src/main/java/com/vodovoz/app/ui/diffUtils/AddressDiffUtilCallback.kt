package com.vodovoz.app.ui.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.AddressUI

class AddressDiffUtilCallback(
    private val oldList: List<AddressUI>,
    private val newList: List<AddressUI>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        if (oldItem.house != newItem.house) return false
        if (oldItem.street != newItem.street) return false
        if (oldItem.locality != newItem.locality) return false
        if (oldItem.fullAddress != newItem.fullAddress) return false
        if (oldItem.comment != newItem.comment) return false
        if (oldItem.email != newItem.email) return false
        if (oldItem.entrance != newItem.entrance) return false
        if (oldItem.flat != newItem.flat) return false
        if (oldItem.floor != newItem.floor) return false
        if (oldItem.name != newItem.name) return false
        if (oldItem.phone != newItem.phone) return false
        if (oldItem.type != newItem.type) return false

        return true
    }
}