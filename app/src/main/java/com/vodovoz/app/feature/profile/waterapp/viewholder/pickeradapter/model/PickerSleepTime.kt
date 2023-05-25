package com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model

import com.vodovoz.app.common.content.itemadapter.Item

data class PickerSleepTime(
    val id: Int
) : Item {

    override fun getItemViewType(): Int {
        return 3
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is PickerSleepTime) return false

        return id == item.id
    }

}
