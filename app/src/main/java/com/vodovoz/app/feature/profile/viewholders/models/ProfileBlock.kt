package com.vodovoz.app.feature.profile.viewholders.models

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.profile.cats.BLOCKRESPONSE

data class ProfileBlock(
    val id: Int = 2,
    val data: List<BLOCKRESPONSE>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.item_profile_block
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ProfileBlock) return false

        return id == item.id
    }

}