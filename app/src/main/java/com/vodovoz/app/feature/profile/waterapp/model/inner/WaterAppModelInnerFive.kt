package com.vodovoz.app.feature.profile.waterapp.model.inner

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class WaterAppModelInnerFive(
    val id: Int = 5
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.fragment_water_app_inner_fifth
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is WaterAppModelInnerFive) return false

        return id == item.id
    }

}