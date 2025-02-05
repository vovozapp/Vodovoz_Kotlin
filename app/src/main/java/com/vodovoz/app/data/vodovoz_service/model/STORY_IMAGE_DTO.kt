package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class STORY_IMAGE_DTO(
    @Json(name = "IMAGE")
    val IMAGE: String?
)