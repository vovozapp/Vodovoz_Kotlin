package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class KNOPKA(
    @Json(name = "COLOR_BACKGROUND")
    val T_COLORBACKGROUND: String?,
    @Json(name = "COLOR_TEXT")
    val T_COLORTEXT: String?,
    @Json(name = "NAME")
    val T_NAME: String?
)