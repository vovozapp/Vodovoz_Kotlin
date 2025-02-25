package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class SOGLASHENIE_DTO(
    @Json(name = "TEXT")
    val TEXT: String?,
    @Json(name = "ZAGOLOVOKi")
    val ZAGOLOVOKi: List<String?>?
)