package com.vodovoz.app.data.vodovoz_service.model


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class PromotionsDTO(
    @Json(name = "DATA")
    val DATA: List<PROMOTION_DATA_DTO?>?,
    @Json(name = "KNOPKA")
    val KNOPKA: KNOPKA_DTO?,
    @Json(name = "RAZDELI")
    val RAZDELI: List<PROMOTION_RAZDEL_DTO?>?,
    @Json(name = "TITLE")
    val TITLE: String?,
)