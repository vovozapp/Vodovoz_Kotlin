package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShippingIntervalUI(
    val id: Long = 0,
    val name: String = ""
): Parcelable, Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_shipping_interval
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ShippingIntervalUI) return false

        return this == item
    }

}