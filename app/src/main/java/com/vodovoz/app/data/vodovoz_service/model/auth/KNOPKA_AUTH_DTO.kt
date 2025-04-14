package com.vodovoz.app.data.vodovoz_service.model.auth


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class KNOPKA_AUTH_DTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "TEXTCOLOR")
    val TEXTCOLOR: String?,
    @Json(name = "BACKGROUND")
    val BACKGROUND: String?,
    @Json(name = "ID")
    val ID: String?
)