package com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model

import com.vodovoz.app.common.content.itemadapter.Item

data class PickerWeight(
    val id: Int
) : Item {

    override fun getItemViewType(): Int {
        return 2
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is PickerWeight) return false

        return id == item.id
    }

}
