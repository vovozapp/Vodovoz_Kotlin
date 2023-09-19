package com.vodovoz.app.feature.profile.waterapp.model.inner

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class WaterAppModelInnerThree(
    val id: Int = 3
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.fragment_water_app_inner_third
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is WaterAppModelInnerThree) return false

        return id == item.id
    }

}