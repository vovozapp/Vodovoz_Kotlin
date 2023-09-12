package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
class FilterUI(
    val code: String,
    val name: String,
    var filterValueList: MutableList<FilterValueUI> = mutableListOf(),
) : Parcelable, Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_filter
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is FilterUI) return false

        return code == item.code
    }
}