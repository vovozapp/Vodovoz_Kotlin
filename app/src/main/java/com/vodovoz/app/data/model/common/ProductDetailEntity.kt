package com.vodovoz.app.data.model.common

class ProductDetailEntity(
    val id: Long,
    val name: String,
    val previewText: String,
    val detailText: String,
    val shareUrl: String = "",
    var isFavorite: Boolean,
    val leftItems: Int = 0,
    val youtubeVideoCode: String = "",
    val rutubeVideoCode: String = "",
    val rating: String = "0",
    val isAvailable: Boolean,
    val status: String = "",
    val statusColor: String = "",
    val labels: List<LabelEntity> = emptyList(),
    val consumerInfo: String,
    val commentsAmount: Int,
    val commentsAmountText: String = "",
    val pricePerUnit: String = "",
    var cartQuantity: Int = 0,
    val brandEntity: BrandEntity? = null,
    val propertiesGroupEntityList: List<PropertiesGroupEntity> = listOf(),
    val priceEntityList: List<PriceEntity> = listOf(),
    val detailPictureList: List<String> = listOf(),
    val blockList: List<BlockEntity> = listOf()
)