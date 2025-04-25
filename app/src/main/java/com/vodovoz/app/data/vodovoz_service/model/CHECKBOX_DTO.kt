package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class CHECKBOX_DTO(
    @Json(name = "VALUE")
    val VALUE: String?,
    @Json(name = "ID")
    val GROUP_ID: String?
)