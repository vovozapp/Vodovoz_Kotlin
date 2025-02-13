package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class SORTIROKA_DTO(
    @Json(name = "DANNIESORT")
    val DANNIESORT: List<SORT_DTO?>?,
    @Json(name = "NAMEGLAV")
    val NAMEGLAV: String?
)