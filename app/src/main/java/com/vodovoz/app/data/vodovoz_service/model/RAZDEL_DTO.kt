package com.vodovoz.app.data.vodovoz_service.model


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class RAZDEL_DTO(
    @Json(name = "DATA")
    val DATA: List<TOVAR_DATA_DTO?>?,
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "KNOPKA")
    val KNOPKA: KNOPKA_DTO?
)