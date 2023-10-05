package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
class FilterUI(
    val code: String,
    val name: String,
    val type: String? = null,
    val values: ValuesUI? = null,
    var filterValueList: MutableList<FilterValueUI> = mutableListOf(),
) : Parcelable, Item {

    override fun getItemViewType(): Int {
        return if (type == "range_filter") {
            R.layout.view_holder_filter_range
        } else {
            R.layout.view_holder_filter
        }
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is FilterUI) return false

        return code == item.code
    }
}

@Parcelize
class ValuesUI(
    val min: Float,
    val max: Float,
) : Parcelable