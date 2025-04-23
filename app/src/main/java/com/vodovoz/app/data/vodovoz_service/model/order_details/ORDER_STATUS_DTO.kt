package com.vodovoz.app.data.vodovoz_service.model.order_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ORDER_STATUS_DTO(
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "ID")
    val ID: String?
)