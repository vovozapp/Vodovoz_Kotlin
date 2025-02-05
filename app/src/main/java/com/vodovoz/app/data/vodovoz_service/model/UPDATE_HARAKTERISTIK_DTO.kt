package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class UPDATE_HARAKTERISTIK_DTO(
    @Json(name = "ACTION")
    val T_ACTION: String?,
    @Json(name = "ID")
    val T_ID: Int?,
    @Json(name = "KNOPKA")
    val T_KNOPKA: KNOPKA?
)