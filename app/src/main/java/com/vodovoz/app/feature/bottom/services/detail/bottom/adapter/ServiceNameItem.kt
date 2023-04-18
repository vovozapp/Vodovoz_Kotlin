package com.vodovoz.app.feature.bottom.services.detail.bottom.adapter

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class ServiceNameItem(
    val name: String,
    val type: String,
    val isSelected: Boolean = false
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_service_name
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ServiceNameItem) return false

        return this == item
    }

}
