package com.vodovoz.app.feature.profile.viewholders.models

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class ProfileLogout(
    val id: Int = 5,
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.item_profile_logout
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ProfileLogout) return false

        return id == item.id
    }

}
