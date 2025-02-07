package com.vodovoz.app.data.vodovoz_service.model.product_details


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class BLOK_KNOPKA_DIZAIN_DTO(
    @Json(name = "BLOCK")
    val BLOCK: BLOCK_U_BLOCK_KNOPKA_DIZAIN_DTO?,
    @Json(name = "DATA")
    val DATA: BLOCK_KNOPKA_DATA_DTO?,
    @Json(name = "KNOPKA_KUPIT")
    val KNOPKA_KUPIT: KNOPKA_KUPIT_DTO?,
)