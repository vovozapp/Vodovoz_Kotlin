package com.vodovoz.app.data.vodovoz_service.model.order_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ORDER_DETAILS_ITOG_DTO(
    @Json(name = "Итого")
    val finalPrice: String?,
    @Json(name = "Товаров на сумму")
    val productsPrice: String?,
    @Json(name = "Залог")
    val deposit: String?,
    @Json(name = "Доставка")
    val delivery: String?,
    @Json(name = "Оплата парковки")
    val parking: String?
)