package com.vodovoz.app.data.vodovoz_service.model.certificate


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class KNOPKA(
    @Json(name = "TEXT")
    val _VODTEXT: String?,
    @Json(name = "COLOR")
    val _VODCOLOR: String?,
    @Json(name = "BACKGROUND")
    val _VODBACKGROUND: String?,
    @Json(name = "ID")
    val _VODID: String?
)