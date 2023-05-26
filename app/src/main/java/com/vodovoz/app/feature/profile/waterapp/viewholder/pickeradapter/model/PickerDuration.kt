package com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model

import com.vodovoz.app.common.content.itemadapter.Item

data class PickerDuration(
    val id: Int
) : Item {

    override fun getItemViewType(): Int {
        return 5
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is PickerDuration) return false

        return id == item.id
    }

}
