package com.vodovoz.app.data.vodovoz_service.model.profile


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class PROFILE_NORMAL_MENU_DTO(
    @Json(name = "TEXT")
    val TEXT: String?,
    @Json(name = "IMAGE")
    val IMAGE: String?,
    @Json(name = "ID")
    val ID: String?,
    @Json(name = "TEXT_OKNO")
    val TEXT_OKNO: PROFILE_MENO_OKNO_DTO?,
    @Json(name = "OPISANIE")
    val OPISANIE: String?
)

