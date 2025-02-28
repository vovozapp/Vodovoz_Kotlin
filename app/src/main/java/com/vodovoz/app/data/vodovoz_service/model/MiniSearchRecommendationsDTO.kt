package com.vodovoz.app.data.vodovoz_service.model

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class MiniSearchRecommendationsDTO(
    @Json(name = "SLOVA")
    val SLOVA: List<String>?,
    @Json(name = "TOVARY")
    val TOVARY: TOVARY_DTO?,
)