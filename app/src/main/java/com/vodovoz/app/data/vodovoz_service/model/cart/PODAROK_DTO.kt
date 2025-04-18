package com.vodovoz.app.data.vodovoz_service.model.cart


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class PODAROK_DTO(
    @Json(name = "ID")
    val ID: Long?,
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "OPIS")
    val OPIS: String?,
    @Json(name = "MAXSYMMA")
    val MAXSYMMA: Int?,
    @Json(name = "KARTINKA")
    val KARTINKA: String?,
    @Json(name = "KNOPKA")
    val KNOPKA: PODAROK_KNOPKA_DTO?,
    @Json(name = "OKNOPODAROK")
    val OKNOPODAROK: OKNO_PODAROK_DTO?
)