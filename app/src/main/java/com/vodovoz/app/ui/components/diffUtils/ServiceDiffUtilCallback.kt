package com.vodovoz.app.ui.components.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.ui.model.ServiceUI

class ServiceDiffUtilCallback(
    private val oldList: List<ServiceUI>,
    private val newList: List<ServiceUI>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].type == newList[newItemPosition].type

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        if (oldItem.detailPicture != newItem.detailPicture) return false
        if (oldItem.name != newItem.name) return false
        if (oldItem.detail != newItem.detail) return false
        if (oldItem.price != newItem.price) return false

        return true
    }
}