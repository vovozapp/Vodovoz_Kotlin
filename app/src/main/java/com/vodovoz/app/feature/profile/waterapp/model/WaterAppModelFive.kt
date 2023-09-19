package com.vodovoz.app.feature.profile.waterapp.model

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class WaterAppModelFive(
    val id: Int = 5
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.fragment_water_app_fifth
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is WaterAppModelFive) return false

        return id == item.id
    }

}