package com.vodovoz.app.data.vodovoz_service.model.order_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ABOUT_ORDER_OKNO_DTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "DANNYE")
    val DANNYE: List<ABOUT_ORDER_ITEM_DTO>?
)