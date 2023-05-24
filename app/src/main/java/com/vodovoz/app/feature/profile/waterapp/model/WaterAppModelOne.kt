package com.vodovoz.app.feature.profile.waterapp.model

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts

data class WaterAppModelOne(
    val id: Int = 1
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.fragment_water_app_first
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is WaterAppModelOne) return false

        return id == item.id
    }

}