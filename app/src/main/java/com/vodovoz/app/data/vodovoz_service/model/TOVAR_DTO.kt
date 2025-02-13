package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class TOVAR_DTO(
    @Json(name = "DATA")
    val DATA: List<TOVAR_DATA_DTO?>?,
    @Json(name = "NAMETOVAR")
    val NAMETOVAR: String?,
    @Json(name = "SORTIROKA")
    val SORTIROKA: SORTIROVKA_DTO?,
    @Json(name = "STRANIC")
    val STRANIC: Int?
)