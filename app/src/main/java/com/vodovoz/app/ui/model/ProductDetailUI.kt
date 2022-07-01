package com.vodovoz.app.ui.model

class ProductDetailUI(
    val id: Long,
    val name: String,
    val previewText: String,
    val detailText: String,
    val isFavorite: Boolean,
    val youtubeVideoCode: String? = null,
    val rating: Double,
    val status: String? = null,
    val statusColor: String? = null,
    val consumerInfo: String,
    val pricePerUnit: Int? = null,
    val commentsAmount: Int,
    val brandUI: BrandUI? = null,
    val cartAmount: Int,
    val propertiesGroupUIList: List<PropertiesGroupUI> = listOf(),
    val priceUIList: List<PriceUI> = listOf(),
    val detailPictureList: List<String> = listOf()
)