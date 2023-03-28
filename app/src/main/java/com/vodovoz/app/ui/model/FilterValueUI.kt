package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterValueUI(
    val id: String? = null,
    val value: String,
    var isSelected: Boolean = false
) : Parcelable, Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_brand_filter_value
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is FilterValueUI) return false

        return id == item.id
    }

}