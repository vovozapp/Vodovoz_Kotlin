package com.vodovoz.app.feature.profile.waterapp.model.inner

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class WaterAppModelInnerSix(
    val id: Int = 6
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.fragment_water_app_inner_sixth
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is WaterAppModelInnerSix) return false

        return id == item.id
    }

}