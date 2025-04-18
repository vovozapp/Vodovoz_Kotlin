package com.vodovoz.app.data.vodovoz_service.model.cart

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class OKNO_PROMOKOD_DTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "TEXT_V_POLE")
    val TEXT_V_POLE: String?,
    @Json(name = "KNOPKA")
    val KNOPKA: KNOPKA_OKNO_PROMOKOD_DTO?,
    @Json(name = "VALUE")
    val VALUE: OKNO_PROMOKOD_VALUE?
)

@Keep
data class KNOPKA_OKNO_PROMOKOD_DTO(
    @Json(name = "TITLE")
    val TITLE: String?
)

@Keep
data class OKNO_PROMOKOD_VALUE(
    @Json(name = "OSHIBKA")
    val OSHIBKA: String?
)
