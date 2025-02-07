package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class BLOK_KNOPKA_DTO(
    @Json(name = "DATA")
    val DATA: BLOCK_KNOPKA_DATA_DTO?,
    @Json(name = "KNOPKA")
    val KNOPKA: BLOCK_KNOPKA_VALUE_DTO?,
    @Json(name = "KNOPKA_KUPIT")
    val KNOPKA_KUPIT: KNOPKA_KUPIT_DTO?
)