package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class NALICHIE_MORE_DTO(
    @Json(name = "CVET")
    val CVET: String?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "BACKGROUND")
    val BACKGROUND: String?
)