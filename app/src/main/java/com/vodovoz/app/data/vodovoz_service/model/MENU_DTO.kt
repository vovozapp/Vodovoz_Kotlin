package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class MENU_DTO(
    @Json(name = "BORDERCOLOR")
    val BORDERCOLOR: String?,
    @Json(name = "IDKLYCH")
    val IDKLYCH: String?,
    @Json(name = "KARTINKA")
    val KARTINKA: String?,
    @Json(name = "OPISANIE")
    val OPISANIE: String?,
    @Json(name = "TITLE")
    val TITLE: String?
)