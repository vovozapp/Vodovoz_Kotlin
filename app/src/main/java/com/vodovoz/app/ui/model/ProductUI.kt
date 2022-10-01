package com.vodovoz.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductUI(
    val id: Long,
    val name: String,
    var detailPicture: String,
    var isFavorite: Boolean,
    var leftItems: Int,
    val pricePerUnit: Int,
    val priceList: List<PriceUI>,
    val status: String,
    val statusColor: String,
    val rating: Double,
    val isBottle: Boolean,
    val isGift: Boolean,
    var isAvailable: Boolean,
    val commentAmount: String,
    var cartQuantity: Int = 0,
    var orderQuantity: Int = 0,
    val depositPrice: Int = 0,
    val detailPictureList: List<String> = listOf(),
    val replacementProductUIList: List<ProductUI> = listOf()
) : Parcelable

