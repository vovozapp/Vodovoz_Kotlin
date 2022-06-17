package com.vodovoz.app.ui.model

data class ProductUI(
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