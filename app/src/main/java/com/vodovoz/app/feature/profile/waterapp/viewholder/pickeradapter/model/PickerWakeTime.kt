package com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model

import com.vodovoz.app.common.content.itemadapter.Item

data class PickerWakeTime(
    val id: Int
) : Item {

    override fun getItemViewType(): Int {
        return 4
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is PickerWakeTime) return false

        return id == item.id
    }

}
