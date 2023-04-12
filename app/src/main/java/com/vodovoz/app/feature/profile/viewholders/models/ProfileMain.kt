package com.vodovoz.app.feature.profile.viewholders.models

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.profile.cats.ProfileCategoryUI

data class ProfileMain(
    val id: Int = 3,
    val items: List<ProfileCategoryUI>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.item_profile_main_rv
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ProfileMain) return false

        return id == item.id
    }

}
