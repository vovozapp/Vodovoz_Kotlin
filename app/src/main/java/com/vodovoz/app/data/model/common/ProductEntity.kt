package com.vodovoz.app.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ProductEntity(
    val id: Long = 0,
    var cartQuantity: Int = 0,
    var orderQuantity: Int = 0,
    val depositPrice: Int = 0,
    val rating: Double = 0.0,
    var leftItems: Int = 0,
    val pricePerUnit: String = "",
    val name: String = "",
    var detailPicture: String = "",
    val commentAmount: String = "",
    val status: String = "",
    val statusColor: String = "",
    val isBottle: Boolean = false,
    val isGift: Boolean = false,
    var isFavorite: Boolean = false,
    var isAvailable: Boolean = true,
    val priceList: List<PriceEntity> = listOf(),
    val detailPictureList: List<String> = listOf(),
    val replacementProductEntityList: List<ProductEntity> = listOf(),
    val chipsBan: Int? = null,
    val totalDisc: Double = 0.0,
    val conditionalPrice: String = "",
    val condition: String = "",
): Parcelable