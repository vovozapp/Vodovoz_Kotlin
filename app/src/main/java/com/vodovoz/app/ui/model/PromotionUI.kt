package com.vodovoz.app.ui.model

class PromotionUI(
    val id: Long,
    val name: String,
    val detailPicture: String,
    val customerCategory: String? = null,
    val statusColor: String? = null,
    val timeLeft: String,
    val productUIList: List<ProductUI>
)