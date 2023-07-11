package com.vodovoz.app.feature.home.viewholders.homepromotions

import com.vodovoz.app.feature.home.viewholders.homepromotions.model.PromotionAdvEntity

interface PromotionsClickListener {
    fun onPromotionClick(id: Long)
    fun onShowAllPromotionsClick()
    fun onPromotionAdvClick(promotionAdvEntity: PromotionAdvEntity?) {}
}