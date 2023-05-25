package com.vodovoz.app.feature.profile.waterapp.model.inner

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts

data class WaterAppModelInnerOne(
    val id: Int = 1,
    val gender: String = "man"
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.fragment_water_app_inner_first
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is WaterAppModelInnerOne) return false

        return id == item.id
    }

}