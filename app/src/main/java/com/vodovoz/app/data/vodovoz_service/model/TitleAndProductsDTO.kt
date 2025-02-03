package com.vodovoz.app.data.vodovoz_service.model


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class TitleAndProductsDTO(
    @Json(name = "DATA")
    val DATA: List<TOVAR_DATA_DTO?>?,
    @Json(name = "TITLE")
    val TITLE: String?,
)