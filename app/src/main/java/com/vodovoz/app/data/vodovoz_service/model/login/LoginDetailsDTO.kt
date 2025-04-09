package com.vodovoz.app.data.vodovoz_service.model.login


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.user_data.PROFILE_POLE_DTO

@Keep
data class LoginDetailsDTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "OPISANIE")
    val OPISANIE: String?,
    @Json(name = "DATA")
    val DATA: List<PROFILE_POLE_DTO>?,
    @Json(name = "SOGLASHENIE")
    val SOGLASHENIE: String?,
    @Json(name = "KNOPKA")
    val KNOPKA: List<KNOPKA_AUTH_DTO>?,
)