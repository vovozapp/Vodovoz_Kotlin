package com.vodovoz.app.data.vodovoz_service.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CertificateActivationDetailsDTO(
    @Json(name = "TITLE") val title: String?,
    @Json(name = "POLE") val field: CertificateFieldDTO?,
    @Json(name = "TEXT") val text: String?,
    @Json(name = "TEXTPODKNOPKOY") val textUnderButton: String?,
    @Json(name = "KNOPKA") val button: CertificateButtonDTO?
)


@JsonClass(generateAdapter = true)
data class CertificateFieldDTO(
    @Json(name = "CODE") val CODE: String?,
    @Json(name = "OBYZATELNO") val OBYZATELNO: String?,
    @Json(name = "POLE") val POLE: String?,
    @Json(name = "TEXT_V_POLE") val TEXT_V_POLE: String?
)

@JsonClass(generateAdapter = true)
data class CertificateButtonDTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "BACKGROUND")
    val BACKGROUND: String?
)