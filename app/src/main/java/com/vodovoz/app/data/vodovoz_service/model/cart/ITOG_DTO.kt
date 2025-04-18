package com.vodovoz.app.data.vodovoz_service.model.cart


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ITOG_DTO(
    @Json(name = "Итого")
    val finalPriceText: String?,
    @Json(name = "Товаров на сумму")
    val productsPriceText: String?,
    @Json(name = "Залог")
    val depositText: String?,
    @Json(name = "Скидка")
    val discountText: String?
)