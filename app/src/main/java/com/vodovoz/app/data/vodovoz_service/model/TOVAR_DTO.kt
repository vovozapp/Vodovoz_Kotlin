package com.vodovoz.app.data.vodovoz_service.model


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class TOVAR_DTO(
    @Json(name = "DATA")
    val DATA: List<TOVAR_DATA_DTO?>?,
    @Json(name = "NAMETOVAR")
    val NAMETOVAR: String?,
    @Json(name = "SORTIROKA")
    val SORTIROKA: SORTIROVKA_DTO?,
    @Json(name = "STRANIC")
    val STRANIC: Int?,
)

@Keep
data class TOVARY_DTO(
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "TOVARY")
    val TOVARY: List<TOVAR_DATA_DTO?>?,
)