package com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model

import com.vodovoz.app.common.content.itemadapter.Item

data class PickerHeight(
    val id: Int
) : Item {

    override fun getItemViewType(): Int {
        return 1
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is PickerHeight) return false

        return id == item.id
    }

}
