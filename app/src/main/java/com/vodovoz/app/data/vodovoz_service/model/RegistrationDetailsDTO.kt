package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.auth.KNOPKA_AUTH_DTO

@Keep
data class RegistrationDetailsDTO(
    @Json(name = "DATA")
    val DATA: List<REGISTRATION_FIELD_DTO>?,
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "SOGLASHENIE")
    val SOGLASHENIE: String?,
    @Json(name = "KNOPKA")
    val KNOPKA: List<KNOPKA_AUTH_DTO>?
)