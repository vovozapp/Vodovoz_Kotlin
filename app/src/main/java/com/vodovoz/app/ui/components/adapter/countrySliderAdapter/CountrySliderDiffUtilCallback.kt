package com.vodovoz.app.ui.components.adapter.countrySliderAdapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.CountryUI

class CountrySliderDiffUtilCallback(
    private val oldList: List<CountryUI>,
    private val newList: List<CountryUI>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        if (oldItem.detailPicture != newItem.detailPicture) return false
        if (oldItem.name != newItem.name) return false

        return true
    }
}