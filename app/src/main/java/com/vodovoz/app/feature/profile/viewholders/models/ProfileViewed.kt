package com.vodovoz.app.feature.profile.viewholders.models

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.CategoryDetailUI

data class ProfileViewed(
    val id: Int = 5,
    val items: List<CategoryDetailUI>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.item_profile_viewed
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ProfileViewed) return false

        return id == item.id
    }

}