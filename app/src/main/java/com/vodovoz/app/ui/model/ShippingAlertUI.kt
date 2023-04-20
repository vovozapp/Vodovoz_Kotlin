package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShippingAlertUI(
    val id: Long = 0,
    val name: String = ""
): Parcelable, Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_shipping_alert
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ShippingAlertUI) return false

        return this == item
    }

}