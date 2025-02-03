package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ZAKAZ_DTO(
    @Json(name = "BORDERCOLOR")
    val BORDERCOLOR: String?,
    @Json(name = "IDZAKAZ")
    val IDZAKAZ: Int?,
    @Json(name = "OPISANIE")
    val OPISANIE: String?,
    @Json(name = "PRICE")
    val PRICE: String?,
    @Json(name = "ZAGALOVOK")
    val ZAGALOVOK: String?
)