package com.vodovoz.app.data.model.common

class PromotionEntity(
    val id: Long,
    val name: String,
    val detailPicture: String,
    val customerCategory: String,
    val statusColor: String,
    val timeLeft: String,
    val productEntityList: List<ProductEntity> = listOf()
)