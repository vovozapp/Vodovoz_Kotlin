package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class AKCIYA_DTO(
    @Json(name = "DATAOUT")
    val DATAOUT: DATAOUT_DTO?,
    @Json(name = "DETAIL_PICTURE")
    val DETAIL_PICTURE: String?,
    @Json(name = "DETAIL_TEXT")
    val DETAIL_TEXT: String?,
    @Json(name = "ID")
    val ID: Int?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "OREKLAME")
    val OREKLAME: OREKLAME_DTO?
)