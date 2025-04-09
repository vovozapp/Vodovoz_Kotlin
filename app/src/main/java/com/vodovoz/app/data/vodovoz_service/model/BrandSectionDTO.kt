package com.vodovoz.app.data.vodovoz_service.model

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class BrandSectionDTO(
    @Json(name = "COUNT")
    val COUNT: String? = null,
    @Json(name = "DATA")
    val DATA: List<BrandDTO>? = null,
    @Json(name = "STRANIC")
    val STRANIC: Int? = null,
    @Json(name = "TITLE")
    val TITLE: String? = null,
)