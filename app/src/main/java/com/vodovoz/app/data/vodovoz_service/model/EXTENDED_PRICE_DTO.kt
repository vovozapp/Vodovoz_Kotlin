package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class EXTENDED_PRICE_DTO(
    @Json(name = "OLD_PRICE")
    val OLD_PRICE: Int?,
    @Json(name = "PRICE")
    val PRICE: Int?,
    @Json(name = "QUANTITY_FROM")
    val QUANTITY_FROM: Int?,
    @Json(name = "QUANTITY_TO")
    val QUANTITY_TO: Int?
)