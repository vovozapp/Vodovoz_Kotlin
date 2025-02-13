package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class POLOSKA(
    @Json(name = "BACKROUND")
    val T_BACKROUND: String?,
    @Json(name = "TEXTCOLOR")
    val T_TEXTCOLOR: String?
)