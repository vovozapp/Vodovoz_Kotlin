package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class KNOPKA_KUPIT_DTO(
    @Json(name = "BACKGROUND")
    val BACKGROUND: String?,
    @Json(name = "COLORTEXT")
    val COLORTEXT: String?,
    @Json(name = "DOPTOVAR")
    val DOPTOVAR: String?,
    @Json(name = "IDTOVAR")
    val IDTOVAR: String?,
    @Json(name = "TITLE")
    val TITLE: String?
)