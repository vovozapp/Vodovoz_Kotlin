package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class PayMethodUI(
    val id: Long = 0,
    val name: String = "",
    val isSelected: Boolean = false
) : Parcelable, Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_pay_method
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is PayMethodUI) return false

        return this == item
    }
}