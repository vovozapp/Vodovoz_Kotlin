package com.vodovoz.app.feature.bottom.services.detail.bottom.adapter

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceNameItem(
    val name: String,
    val type: String,
    val isSelected: Boolean = false
) : Item, Parcelable {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_service_name
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ServiceNameItem) return false

        return this == item
    }

}
