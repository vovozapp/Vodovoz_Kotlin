package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class PRICE_DTO(
    @Json(name = "NEW")
    val NEW: String?,
    @Json(name = "OLD")
    val OLD: String?
)