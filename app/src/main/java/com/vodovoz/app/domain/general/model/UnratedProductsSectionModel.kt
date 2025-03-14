package com.vodovoz.app.domain.general.model

data class UnratedProductsSectionModel(
    val title: String,
    val productTitle: String,
    val countProductsText: String,
    val products: List<UnratedProductModel>,
)


data class UnratedProductModel(
    val id: Long,
    val name: String,
    val detailPicture: String
)