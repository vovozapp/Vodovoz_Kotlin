package com.vodovoz.app.data.vodovoz_service.model.cart


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class OKNO_PODAROK_DTO(
    @Json(name = "PODAROK")
    val PODAROK: List<PRODUCT_PRODAROK_DTO>?,
    @Json(name = "KNOPKA")
    val KNOPKA: PODAROK_KNOPKA_DTO?
)