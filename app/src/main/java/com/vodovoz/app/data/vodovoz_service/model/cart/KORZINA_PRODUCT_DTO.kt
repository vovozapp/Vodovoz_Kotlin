package com.vodovoz.app.data.vodovoz_service.model.cart


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class KORZINA_PRODUCT_DTO(
    @Json(name = "ID")
    val ID: Long?,
    @Json(name = "MODULE")
    val MODULE: String?,
    @Json(name = "PRODUCT_ID")
    val PRODUCT_ID: String?,
    @Json(name = "QUANTITY")
    val QUANTITY: String?,
    @Json(name = "CAN_BUY")
    val CAN_BUY: String?,
    @Json(name = "PRICE")
    val PRICE: Float?,
    @Json(name = "WEIGHT")
    val WEIGHT: String?,
    @Json(name = "CURRENCY")
    val CURRENCY: String?,
    @Json(name = "VAT_RATE")
    val VAT_RATE: String?,
    @Json(name = "SUBSCRIBE")
    val SUBSCRIBE: String?,
    @Json(name = "DISCOUNT_PRICE")
    val DISCOUNT_PRICE: Float?,
    @Json(name = "PODROBNO")
    val PODROBNO: KORZINA_PRODUCT_PODROBNO_DTO?,
    @Json(name = "PRODUCT_PRICE_ID")
    val PRODUCT_PRICE_ID: String?,
    @Json(name = "BASE_PRICE")
    val BASE_PRICE: Float?,
    @Json(name = "PROPERTIES")
    val PROPERTIES: List<Any?>?,
    @Json(name = "ACTION_APPLIED")
    val ACTION_APPLIED: String?,
    @Json(name = "VAT_VALUE")
    val VAT_VALUE: Double?,
    @Json(name = "PRICE_FORMATED")
    val PRICE_FORMATED: String?,
    @Json(name = "DISCOUNT_PRICE_PERCENT")
    val DISCOUNT_PRICE_PERCENT: String?,
    @Json(name = "DISCOUNT_PRICE_PERCENT_FORMATED")
    val DISCOUNT_PRICE_PERCENT_FORMATED: String?,
    @Json(name = "DISCOUNTS_APPLY")
    val DISCOUNTS_APPLY: Boolean?
)