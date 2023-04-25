package com.vodovoz.app.feature.profile.viewholders.models

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.CategoryDetailUI

data class ProfileBestForYou(
    val id: Int = 7,
    val data: CategoryDetailUI
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.item_profile_best_for_you
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ProfileBestForYou) return false

        return id == item.id
    }

}
