package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class HARAKTERISTIK_DTO(
    @Json(name = "ACTION")
    val ACTION: String?,
    @Json(name = "ID")
    val ID: String?
)