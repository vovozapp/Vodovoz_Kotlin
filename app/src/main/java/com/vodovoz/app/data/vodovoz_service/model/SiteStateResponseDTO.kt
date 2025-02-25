package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class SiteStateResponseDTO(
    @Json(name = "ACTIVE")
    val ACTIVE: String?,
    @Json(name = "CHATJIVO")
    val CHATJIVO: CHATJIVO_DTO?,
    @Json(name = "COMMENTFILES")
    val COMMENTFILES: Boolean?,
    @Json(name = "DATA")
    val DATA: Any?,
    @Json(name = "GENERATION")
    val GENERATION: GENERATION_DTO?,
    @Json(name = "REGISTRACION_SMS")
    val REGISTRACION_SMS: String?,
    @Json(name = "SMSRASSILKA")
    val SMSRASSILKA: String?,
    @Json(name = "SOGLASHENIE")
    val SOGLASHENIE: SOGLASHENIE_DTO?,
    @Json(name = "TESTSAITSSILKA")
    val TESTSAITSSILKA: String?
)