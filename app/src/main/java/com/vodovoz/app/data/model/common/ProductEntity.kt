package com.vodovoz.app.data.model.common

class ProductEntity(
    val id: Long,
    val name: String,
    var detailPicture: String,
    var isFavorite: Boolean,
    val oldPrice: Int,
    val newPrice: Int,
    val status: String,
    val statusColor: String,
    val rating: Double,
    val commentAmount: String,
    var cartQuantity: Int = 0,
    val isCanReturnBottles: Boolean = false,
    val detailPictureList: List<String>
)