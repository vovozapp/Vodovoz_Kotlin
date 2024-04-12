package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.common.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderFilterUI(
    val id: String,
    val name: String,
    var isChecked: Boolean = false
): Parcelable, Item {
    override fun getItemViewType(): Int {
        return ORDER_STATUS_FILTER_VIEW_TYPE
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if(item !is OrderStatusUI) return false

        return id == item.id
    }
    companion object {

        const val ORDER_STATUS_FILTER_VIEW_TYPE = -1001

        const val ALL_ID = "all"
        const val ALL_NAME = "Все"
    }


}
