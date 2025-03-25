package com.vodovoz.app.data.vodovoz_service.model.profile


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class DENIGI_DTO(
    @Json(name = "BACKGROUND")
    val BACKGROUND: String?,
    @Json(name = "ZAGALOVOK")
    val ZAGALOVOK: ZAGALOVOK_DTO?,
    @Json(name = "OPISANIE")
    val OPISANIE: OPISANIE_DTO?,
    @Json(name = "IMAGE")
    val IMAGE: String?,
    @Json(name = "ID")
    val ID: String?,
    @Json(name = "TEXT_OKNO")
    val TEXT_OKNO: DENIGI_TEXT_OKNO_DTO?
)