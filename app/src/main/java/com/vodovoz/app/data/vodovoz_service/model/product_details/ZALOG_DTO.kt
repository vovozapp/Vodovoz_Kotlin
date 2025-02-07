package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ZALOG_DTO(
    @Json(name = "OPISANIE")
    val OPISANIE: ZALOG_OPISANIE_DTO?,
    @Json(name = "PRICE")
    val PRICE: Int?
)