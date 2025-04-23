package com.vodovoz.app.data.vodovoz_service.model.order_details


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.COLORFUL_KNOPKA_DTO

@Keep
data class OrderDetailsDTO(
    @Json(name = "TITLE")
    val TITLE: ORDER_DETAILS_TITLE_DTO?,
    @Json(name = "BLOCK")
    val BLOCK: ORDER_DETAILS_BLOCK_DTO?,
    @Json(name = "TOVARY")
    val TOVARY: ORDER_DETAILS_TOVARY_DTO?,
    @Json(name = "ITOG")
    val ITOG: ORDER_DETAILS_ITOG_DTO?,
    @Json(name = "KNOPKI_NIZ")
    val KNOPKI_NIZ: List<COLORFUL_KNOPKA_DTO>?
)