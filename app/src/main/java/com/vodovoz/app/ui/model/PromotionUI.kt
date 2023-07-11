package com.vodovoz.app.ui.model

import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.home.viewholders.homepromotions.model.PromotionAdvEntity

data class PromotionUI(
    val id: Long,
    val name: String,
    val detailPicture: String,
    val customerCategory: String? = null,
    val statusColor: String? = null,
    val timeLeft: String,
    val productUIList: List<ProductUI>,
    val promotionAdvEntity: PromotionAdvEntity? = null
) : Item {

    override fun getItemViewType(): Int {
        return PROMOTION_UI_VIEW_TYPE
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is PromotionUI) return false

        return id == item.id
    }

    companion object {
        const val PROMOTION_UI_VIEW_TYPE = -41414
    }
}