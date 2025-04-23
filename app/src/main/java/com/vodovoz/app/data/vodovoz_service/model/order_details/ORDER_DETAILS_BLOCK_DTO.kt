package com.vodovoz.app.data.vodovoz_service.model.order_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ORDER_DETAILS_BLOCK_DTO(
    //todo - do this
    @Json(name = "GLAV")
    val GLAV: String?,
    //todo - do this
    @Json(name = "STATUS")
    val STATUS: ORDER_STATUS_DTO?,
    @Json(name = "STATUSY")
    val STATUSY: List<ORDER_STATUS_DTO>?,
    @Json(name = "KNOPKI")
    val KNOPKI: List<ORDER_DETAILS_KNOPKA_DTO>?
)