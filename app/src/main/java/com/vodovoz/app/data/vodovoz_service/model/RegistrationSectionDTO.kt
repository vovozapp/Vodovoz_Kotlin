package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class RegistrationSectionDTO(
    @Json(name = "DATA")
    val DATA: List<REGISTRATION_FIELD_DTO>?,
    @Json(name = "TITLE")
    val TITLE: String?
)