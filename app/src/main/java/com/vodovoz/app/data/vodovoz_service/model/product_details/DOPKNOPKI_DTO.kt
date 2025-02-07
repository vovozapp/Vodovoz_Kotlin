package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class DOPKNOPKI_DTO(
    @Json(name = "BLOK_KNOPKA")
    val BLOK_KNOPKA: BLOK_KNOPKA_DTO?,
    @Json(name = "BLOK_KNOPKA_DIZAIN")
    val BLOK_KNOPKA_DIZAIN: BLOK_KNOPKA_DIZAIN_DTO?
)