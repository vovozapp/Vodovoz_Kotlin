package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class SORT_DTO(
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "SORT")
    val SORT: String?,
    @Json(name = "ZNACHIE")
    val ZNACHIE: String?
)