package com.vodovoz.app.ui.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.BannerUI
import com.yandex.mapkit.geometry.Point

class SimpleAddressDiffUtilCallback(
    private val oldList: List<Pair<String, Point>>,
    private val newList: List<Pair<String, Point>>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].first == newList[newItemPosition].first

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        if (oldItem.second.latitude != newItem.second.latitude) return false
        if (oldItem.second.longitude != newItem.second.longitude) return false

        return true
    }
}