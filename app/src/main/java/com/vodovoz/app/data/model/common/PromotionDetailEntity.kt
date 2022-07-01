package com.vodovoz.app.data.model.common

class PromotionDetailEntity(
    val id: Long,
    val name: String,
    val detailText: String,
    val status: String,
    val statusColor: String,
    val detailPicture: String,
    val timeLeft: String,
    val promotionCategoryDetailEntity: CategoryDetailEntity? = null,
    val forYouCategoryDetailEntity: CategoryDetailEntity
)