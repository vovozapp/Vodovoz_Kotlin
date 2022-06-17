package com.vodovoz.app.ui.model

class PromotionUI(
    val id: Long,
    val name: String,
    val detailPicture: String,
    val customerCategory: String,
    val statusColor: String,
    val timeLeft: String,
    val productUIList: List<ProductUI>
)