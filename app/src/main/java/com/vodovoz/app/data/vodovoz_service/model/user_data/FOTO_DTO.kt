package com.vodovoz.app.data.vodovoz_service.model.user_data


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class FOTO_DTO(
    @Json(name = "IMG")
    val IMG: String?,
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "OPISANIE")
    val OPISANIE: String?
)