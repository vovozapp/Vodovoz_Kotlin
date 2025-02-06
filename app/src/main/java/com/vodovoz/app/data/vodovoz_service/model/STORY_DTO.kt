package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class STORY_DTO(
    @Json(name = "ID")
    val ID: Long?,
    @Json(name = "RAZDEL")
    val RAZDEL: STORY_IMAGE_DTO?,
    @Json(name = "VNYTRENNOST")
    val VNYTRENNOST: List<VNYTRENNOST_DTO?>?,
    @Json(name = "VNYTRENNOSCOUNT")
    val VNYTRENNOSTCOUNT: Int?
)