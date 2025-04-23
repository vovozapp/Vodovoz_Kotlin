package com.vodovoz.app.domain.general.model.order

import com.vodovoz.app.domain.general.model.LabelModel
import com.vodovoz.app.domain.general.model.PriceModel

data class OrderProductModel(
    val id: Long,
    val name: String,
    val quantity: Int,
    val articleNumberText: String,
    val depositText: String?,
    val price: PriceModel?,
    val isShowcaseProduct: Boolean,
    val image: String,
    val labels: List<LabelModel>,
    val pricePerUnit: Int?,
    val unitOfMeasurement: String?,
    val catalogQuantity: Int,
    val isFavorite: Boolean,
    val present: OrderProductPresentModel?,
    val restrictionCode: Int
)

data class OrderProductPresentModel(
    val title: String,
    val color: String,
)
