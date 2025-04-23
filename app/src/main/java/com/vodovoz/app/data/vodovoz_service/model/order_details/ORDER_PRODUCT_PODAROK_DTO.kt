package com.vodovoz.app.data.vodovoz_service.model.order_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ORDER_PRODUCT_PODAROK_DTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "COLOR")
    val COLOR: String?
)