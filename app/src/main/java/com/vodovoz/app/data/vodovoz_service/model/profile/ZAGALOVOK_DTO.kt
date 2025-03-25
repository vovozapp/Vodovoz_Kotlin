package com.vodovoz.app.data.vodovoz_service.model.profile


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ZAGALOVOK_DTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "TEXTCOLOR")
    val TEXTCOLOR: String?
)