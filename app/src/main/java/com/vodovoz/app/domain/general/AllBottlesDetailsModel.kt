package com.vodovoz.app.domain.general

data class AllBottlesDetailsModel(
    val description: String,
    val isSingleBottleMode: Boolean,
    val bottles: List<BottleModel>
)

data class BottleModel(
    val name: String,
    val id: Long,
    val articleText: String,
    val description: String,
    val cartQuantity: Int
)
