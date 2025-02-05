package com.vodovoz.app.data.vodovoz_service.model


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class UPDATE_HARAKTERISTIK_DTO(
    @Json(name = "KNOPKA")
    val KNOPKA: COLORFUL_KNOPKA_DTO?,
)