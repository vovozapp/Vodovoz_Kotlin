package com.vodovoz.app.ui.model

import com.vodovoz.app.R
import com.vodovoz.app.ui.base.content.itemadapter.Item

data class PromotionUI(
    val id: Long,
    val name: String,
    val detailPicture: String,
    val customerCategory: String? = null,
    val statusColor: String? = null,
    val timeLeft: String,
    val productUIList: List<ProductUI>
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_slider_promotion
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is PromotionUI) return false

        return id == item.id
    }
}