package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class PresentDTO(
    @Json(name = "PEREXOD")
    val PEREXOD: Any?,
    @Json(name = "POLOSKA")
    val POLOSKA: POLOSKA?,
    @Json(name = "SYMMAZAKAZA")
    val SYMMAZAKAZA: Any?,
    @Json(name = "TEXT")
    val TEXT: String?
)