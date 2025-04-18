package com.vodovoz.app.data.vodovoz_service.model.cart


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class KNOPKA_PROMOKOD_DTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "IMAGE")
    val IMAGE: String?,
    @Json(name = "VALUE")
    val VALUE: KNOPKA_VALUE_PROMOKOD_DTO?,
    @Json(name = "ID")
    val ID: String?,
    @Json(name = "OKNO")
    val OKNO: OKNO_PROMOKOD_DTO?
)

@Keep
data class KNOPKA_VALUE_PROMOKOD_DTO(
    @Json(name = "COUPON")
    val COUPON: String,
    @Json(name = "TEXT")
    val TEXT: KNOPKA_VALUE_TEXT_PROMOKOD_DTO
)

@Keep
data class KNOPKA_VALUE_TEXT_PROMOKOD_DTO(
    @Json(name = "TITLE")
    val TITLE: String,

    @Json(name = "COLOR")
    val COLOR: String
)