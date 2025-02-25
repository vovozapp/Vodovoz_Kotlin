package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class POLOSKA_DTO(
    @Json(name = "BACKROUND")
    val BACKROUND: String?,
    @Json(name = "TEXTCOLOR")
    val TEXTCOLOR: String?
)