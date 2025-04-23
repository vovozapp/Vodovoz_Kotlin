package com.vodovoz.app.data.vodovoz_service.model.order_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ORDER_DETAILS_TOVARY_DTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "TOVAR")
    val TOVAR: List<ORDER_DETAILS_TOVAR_DTO>?
)