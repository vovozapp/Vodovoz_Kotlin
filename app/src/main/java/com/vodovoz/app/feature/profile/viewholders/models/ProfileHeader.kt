package com.vodovoz.app.feature.profile.viewholders.models

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.UserDataUI

data class ProfileHeader(
    val id: Int = 1,
    val data: UserDataUI
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.item_profile_header
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ProfileHeader) return false

        return id == item.id
    }

}
