package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class GENERATION_DTO(
    @Json(name = "TIME")
    val TIME: String?,
    @Json(name = "TRAKING")
    val TRAKING: String?
)