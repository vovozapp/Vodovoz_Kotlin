package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class SPECTIAL_PROMOTION_DTO(
    @Json(name = "HARAKTERISTIK")
    val HARAKTERISTIK: VNYTRENNOST_DTO?,
    @Json(name = "ID")
    val ID: Int?,
    @Json(name = "KARTINKA")
    val KARTINKA: String?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "TEXT")
    val TEXT: Any?
)