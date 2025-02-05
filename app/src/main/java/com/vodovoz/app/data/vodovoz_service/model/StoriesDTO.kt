package com.vodovoz.app.data.vodovoz_service.model

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class StoriesDTO(
    @Json(name = "COUNT")
    val count: Int?,
    @Json(name = "DATA")
    val data: List<STORY_DTO>?,
)