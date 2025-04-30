package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class USLOVIE_DTO(
    @Json(name = "ID")
    val ID: String?,
    @Json(name = "TEXT")
    val TEXT: String?,
    @Json(name = "URL")
    val URL: String?
)