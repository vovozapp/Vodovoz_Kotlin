package com.vodovoz.app.ui.components.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.ProductUI

class ProductDiffUtilCallback(
    private val oldList: List<ProductUI>,
    private val newList: List<ProductUI>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        if (oldItem.name != newItem.name) return false
        if (oldItem.detailPicture != newItem.detailPicture) return false
        if (oldItem.oldPrice != newItem.oldPrice) return false
        if (oldItem.newPrice != newItem.newPrice) return false
        if (oldItem.rating != newItem.rating) return false
        if (oldItem.status != newItem.status) return false
        if (oldItem.statusColor != newItem.statusColor) return false

        return true
    }
}