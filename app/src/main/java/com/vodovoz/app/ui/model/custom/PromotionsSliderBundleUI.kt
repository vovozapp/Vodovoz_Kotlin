package com.vodovoz.app.ui.model.custom

import com.vodovoz.app.ui.model.PromotionUI

data class PromotionsSliderBundleUI(
    val title: String,
    val containShowAllButton: Boolean,
    val promotionUIList: List<PromotionUI>
)