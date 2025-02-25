package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class CHATJIVO_DTO(
    @Json(name = "ACTIVE")
    val ACTIVE: String?,
    @Json(name = "SSILKA")
    val SSILKA: String?
)