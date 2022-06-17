package com.vodovoz.app.data.model.common

class ProductEntity(
    val id: Long,
    val name: String,
    var detailPicture: String,
    val oldPrice: Int,
    val newPrice: Int,
    val status: String,
    val statusColor: String,
    val rating: Double,
    val commentAmount: String,
    val detailPictureList: List<String>
)