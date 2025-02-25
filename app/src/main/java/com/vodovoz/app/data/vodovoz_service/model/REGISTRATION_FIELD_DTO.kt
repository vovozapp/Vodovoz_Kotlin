package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class REGISTRATION_FIELD_DTO(
    @Json(name = "CODE")
    val CODE: String?,
    @Json(name = "OBYZATELNO")
    val OBYZATELNO: String?,
    @Json(name = "POLE")
    val POLE: String?,
    @Json(name = "TEXT")
    val TEXT: String?
)