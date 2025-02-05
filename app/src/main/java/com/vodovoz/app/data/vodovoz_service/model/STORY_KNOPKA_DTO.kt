package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class STORY_KNOPKA_DTO(
    @Json(name = "COLOR_BACKGROUND")
    val COLOR_BACKGROUND: String?,
    @Json(name = "COLOR_TEXT")
    val COLOR_TEXT: String?,
    @Json(name = "NAME")
    val NAME: String?
)