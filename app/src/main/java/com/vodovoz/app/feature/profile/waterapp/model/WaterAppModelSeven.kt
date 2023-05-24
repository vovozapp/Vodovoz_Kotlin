package com.vodovoz.app.feature.profile.waterapp.model

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts

data class WaterAppModelSeven(
    val id: Int = 7
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.fragment_water_app_seventh
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is WaterAppModelSeven) return false

        return id == item.id
    }

}