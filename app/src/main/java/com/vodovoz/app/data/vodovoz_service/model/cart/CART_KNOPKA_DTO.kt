package com.vodovoz.app.data.vodovoz_service.model.cart


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class CART_KNOPKA_DTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "IMAGE")
    val IMAGE: String?,
    @Json(name = "ID")
    val ID: String?
)