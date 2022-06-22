package com.vodovoz.app.data.model.common

class PromotionEntity(
    val id: Long,
    val name: String,
    val detailPicture: String,
    val customerCategory: String? = null,
    val statusColor: String? = null,
    val timeLeft: String,
    val productEntityList: List<ProductEntity> = listOf()
)