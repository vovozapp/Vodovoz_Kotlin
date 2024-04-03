package com.vodovoz.app.ui.model.custom

import com.vodovoz.app.ui.model.PromotionUI

data class PromotionsActionUI(
    val name: String,
    val promotionUIList: List<PromotionUI> = listOf(),
)
