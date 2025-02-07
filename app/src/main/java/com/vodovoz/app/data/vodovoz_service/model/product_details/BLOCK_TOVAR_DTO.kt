package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class BLOCK_TOVAR_DTO(
    @Json(name = "KARTINKA")
    val KARTINKA: String?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "PRICE")
    val PRICE: PRICE_DTO?
)