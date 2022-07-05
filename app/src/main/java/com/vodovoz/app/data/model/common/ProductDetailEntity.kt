package com.vodovoz.app.data.model.common

class ProductDetailEntity(
    val id: Long,
    val name: String,
    val previewText: String,
    val detailText: String,
    var isFavorite: Boolean,
    val youtubeVideoCode: String? = null,
    val rating: Double,
    val status: String? = null,
    val statusColor: String? = null,
    val consumerInfo: String,
    val commentsAmount: Int,
    val pricePerUnit: Int? = null,
    val cartQuantity: Int = 0,
    val brandEntity: BrandEntity? = null,
    val propertiesGroupEntityList: List<PropertiesGroupEntity> = listOf(),
    val priceEntityList: List<PriceEntity> = listOf(),
    val detailPictureList: List<String> = listOf()
)