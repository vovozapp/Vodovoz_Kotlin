package com.vodovoz.app.feature.profile.waterapp.model

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class WaterAppModelSix(
    val id: Int = 6
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.fragment_water_app_sixth
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is WaterAppModelSix) return false

        return id == item.id
    }

}