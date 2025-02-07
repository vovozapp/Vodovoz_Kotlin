package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class BannerDTO(
    @Json(name = "DETAIL_PICTURE")
    val DETAIL_PICTURE: String?,
    @Json(name = "HARAKTERISTIK")
    val HARAKTERISTIK: ACTION_DTO?,
    @Json(name = "ID")
    val ID: Int?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "OREKLAME")
    val OREKLAME: OREKLAME_DTO?
)