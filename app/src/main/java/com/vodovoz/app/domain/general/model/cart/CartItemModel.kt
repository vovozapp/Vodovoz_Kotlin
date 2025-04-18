package com.vodovoz.app.domain.general.model.cart

import com.vodovoz.app.domain.general.model.LabelModel

data class CartItemModel(
    val id: Long,
    val productId: Long,
    val productName: String,
    val isFavorite: Boolean,
    val isHit: Boolean,
    val canBuy: Boolean,
    val discountPercentsText: String,
    val discountPrice: Float,
    val priceText: String,
    val currentPrice: Float,
    val basePrice: Float,
    val quantity: Int,
    val depositText: String,
    val articleText: String,
    val image: String,
    val leftItems: Int,
    val label: LabelModel?,
    val hasDiscount: Boolean,
    val restrictionsCode: Int
)
