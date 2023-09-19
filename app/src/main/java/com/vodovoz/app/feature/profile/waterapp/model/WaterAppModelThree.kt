package com.vodovoz.app.feature.profile.waterapp.model

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class WaterAppModelThree(
    val id: Int = 3
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.fragment_water_app_third
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is WaterAppModelThree) return false

        return id == item.id
    }

}