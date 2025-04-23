package com.vodovoz.app.data.vodovoz_service.model.order_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ABOUT_ORDER_ITEM_DTO(
    @Json(name = "IMAGE")
    val IMAGE: String?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "OPIS")
    val OPIS: String?
)