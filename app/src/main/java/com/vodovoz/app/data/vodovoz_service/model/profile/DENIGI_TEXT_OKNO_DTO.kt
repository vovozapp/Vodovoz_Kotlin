package com.vodovoz.app.data.vodovoz_service.model.profile


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class DENIGI_TEXT_OKNO_DTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "TEXT")
    val TEXT: String?
)