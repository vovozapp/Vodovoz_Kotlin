package com.vodovoz.app.ui.model

class PromotionDetailUI(
    val id: Long,
    val name: String,
    val detailText: String,
    val status: String,
    val statusColor: String,
    val detailPicture: String,
    val timeLeft: String,
    val promotionCategoryDetailUI: CategoryDetailUI?,
    val forYouCategoryDetailUI: CategoryDetailUI
)