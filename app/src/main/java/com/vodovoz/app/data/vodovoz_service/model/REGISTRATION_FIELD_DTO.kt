package com.vodovoz.app.data.vodovoz_service.model


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class REGISTRATION_FIELD_DTO(
    @Json(name = "CODE")
    val CODE: String?,
    @Json(name = "OBYZATELNO")
    val OBYZATELNO: String?,
    @Json(name = "POLE")
    val POLE: String?,
    @Json(name = "TEXT")
    val TEXT: String?,
    @Json(name = "TEXT_V_POLE")
    val TEXT_V_POLE: String?,
)