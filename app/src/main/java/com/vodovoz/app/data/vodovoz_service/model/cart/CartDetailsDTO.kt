package com.vodovoz.app.data.vodovoz_service.model.cart


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class CartDetailsDTO(
    @Json(name = "PODAROK")
    val PODAROK: PODAROK_DTO?,
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "COUNT")
    val COUNT: String?,
    @Json(name = "KORZINA")
    val KORZINA: List<KORZINA_PRODUCT_DTO>?,
    @Json(name = "KNOPKI")
    val KNOPKI: KORZINA_KNOPKI_DTO?,
    @Json(name = "ITOG")
    val ITOG: ITOG_DTO?
)