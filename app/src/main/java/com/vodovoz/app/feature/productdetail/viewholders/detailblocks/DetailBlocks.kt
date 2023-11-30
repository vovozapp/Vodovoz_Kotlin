package com.vodovoz.app.feature.productdetail.viewholders.detailblocks

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.BlockUI

data class DetailBlocks(
    val id: Int,
    val items: List<BlockUI>,
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.fragment_product_details_blocks
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is DetailBlocks) return false

        return id == item.id
    }
}