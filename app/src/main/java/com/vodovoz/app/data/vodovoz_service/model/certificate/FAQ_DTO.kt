package com.vodovoz.app.data.vodovoz_service.model.certificate


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class FAQ_DTO(
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "KARTINKA")
    val KARTINKA: String?,
    @Json(name = "DATA")
    val DATA: List<FAQ_ITEM_DTO>?
)