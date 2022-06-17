package com.vodovoz.app.ui.components.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.PromotionUI

class PromotionDiffUtilCallback(
    private val oldList: List<PromotionUI>,
    private val newList: List<PromotionUI>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        if (oldItem.detailPicture != newItem.detailPicture) return false
        if (oldItem.customerCategory != newItem.customerCategory) return false
        if (oldItem.statusColor != newItem.statusColor) return false
        if (oldItem.timeLeft != newItem.timeLeft) return false

        return true
    }
}