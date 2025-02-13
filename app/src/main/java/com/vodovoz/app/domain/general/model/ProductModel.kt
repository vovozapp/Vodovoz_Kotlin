package com.vodovoz.app.domain.general.model

data class ProductModel(
    val id: Long,
    val name: String,
    val deposit: Int,
    val isFavorite: Boolean,
    val rating: Float,
    val picture: String,
    val coefficient: Float,
    val quantity: Int,
    val cartQuantity: Int,
    val pricePerUnit: Int?,
    val unitOfMeasurement: String?,
    val firstPrice: PriceModel,
    val prices: List<PriceModel>,
    val labels: List<LabelModel>,
)

data class PriceModel(
    val price: Float,
    val oldPrice: Float,
    val quantityFrom: Int,
    val quantityTo: Int,
)