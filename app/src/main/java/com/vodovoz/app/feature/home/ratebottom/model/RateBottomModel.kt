package com.vodovoz.app.feature.home.ratebottom.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RateBottomModel(
    val status: String,
    val message: String,
    @Json(name = "data")
    val rateBottomData: RateBottomData,
)

@JsonClass(generateAdapter = true)
data class RateBottomData(
    @Json(name = "LISTRAZDEL")
    val productsList: List<ProductRateBottom>,
    @Json(name = "TITLERAZDEL")
    val titleCategory: String,
    @Json(name = "TITLETOVAR")
    val titleProduct: String,
    @Json(name = "VSEGOTOVAR")
    val allProductsCount: String
)

@JsonClass(generateAdapter = true)
data class ProductRateBottom(
    @Json(name = "DETAIL_PICTURE")
    val image: String,
    @Json(name = "ID")
    val id: Int,
    @Json(name = "NAME")
    val name: String
)