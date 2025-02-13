package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.common.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderStatusUI(
    val id: String,
    val color: Int,
    val statusName: String,
    val image: Int,
    var isChecked: Boolean = false,
) : Parcelable, Item {

    override fun getItemViewType(): Int {
        return ORDER_STATUS_VIEW_TYPE
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is OrderStatusUI) return false

        return id == item.id
    }

    companion object {

        const val ORDER_STATUS_VIEW_TYPE = -1001
    }

}
