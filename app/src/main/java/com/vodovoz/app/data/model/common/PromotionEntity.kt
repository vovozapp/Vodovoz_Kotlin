package com.vodovoz.app.data.model.common

import com.vodovoz.app.feature.home.viewholders.homepromotions.model.PromotionAdvEntity

class PromotionEntity(
    val id: Long,
    val name: String,
    val detailPicture: String,
    val customerCategory: String? = null,
    val statusColor: String? = null,
    val timeLeft: String,
    val productEntityList: List<ProductEntity> = listOf(),
    val promotionAdvEntity: PromotionAdvEntity? = null
)