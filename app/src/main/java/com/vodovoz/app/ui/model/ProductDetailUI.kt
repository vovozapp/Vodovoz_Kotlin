package com.vodovoz.app.ui.model

class ProductDetailUI(
    val id: Long = 0,
    val name: String = "",
    val previewText: String = "",
    val shareUrl: String = "",
    val detailText: String = "",
    var isFavorite: Boolean = false,
    val leftItems: Int = 0,
    val youtubeVideoCode: String = "",
    var rating: Float = 0.0f,
    val isAvailable: Boolean = false,
    val status: String = "",
    val statusColor: String = "",
    val consumerInfo: String = "",
    val pricePerUnit: Int = 0,
    val commentsAmount: Int = 0,
    val brandUI: BrandUI? = null,
    var cartQuantity: Int = 0,
    val propertiesGroupUIList: List<PropertiesGroupUI> = listOf(),
    val priceUIList: List<PriceUI> = listOf(),
    val detailPictureList: List<String> = listOf(),
    var oldQuantity: Int = 0
)