package com.vodovoz.app.data.model.common

class ProductEntity(
    val id: Long = 0,
    val name: String = "",
    var detailPicture: String = "",
    var isFavorite: Boolean = false,
    val priceList: List<PriceEntity> = listOf(),
    val status: String = "",
    val statusColor: String = "",
    val rating: Double = 0.0,
    val commentAmount: String = "",
    var cartQuantity: Int = 0,
    var orderQuantity: Int = 0,
    val isCanReturnBottles: Boolean = false,
    val detailPictureList: List<String> = listOf()
)