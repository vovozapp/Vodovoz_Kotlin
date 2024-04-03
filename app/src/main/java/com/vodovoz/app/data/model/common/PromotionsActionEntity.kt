package com.vodovoz.app.data.model.common

data class PromotionsActionEntity(
    val name: String,
    val promotionEntityList: List<PromotionEntity> = listOf(),
)
