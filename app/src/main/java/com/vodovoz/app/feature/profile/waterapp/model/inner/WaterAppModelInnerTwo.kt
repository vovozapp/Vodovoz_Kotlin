package com.vodovoz.app.feature.profile.waterapp.model.inner

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class WaterAppModelInnerTwo(
    val id: Int = 2
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.fragment_water_app_inner_second
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is WaterAppModelInnerTwo) return false

        return id == item.id
    }

}