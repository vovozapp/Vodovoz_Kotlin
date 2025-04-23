package com.vodovoz.app.data.vodovoz_service.model.order_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ORDER_DETAILS_KNOPKA_DTO(
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "IMAGE")
    val IMAGE: String?,
    @Json(name = "COLOR_BACKGROUND")
    val COLOR_BACKGROUND: String?,
    @Json(name = "COLOR_TEXT")
    val COLOR_TEXT: String?,
    @Json(name = "ID")
    val ID: String?,
    @Json(name = "OPIS")
    val OPIS: String?,
    @Json(name = "OKNO")
    val OKNO: ABOUT_ORDER_OKNO_DTO?,
    @Json(name = "BRAYZER")
    val BRAYZER: String?,
    @Json(name = "URL")
    val URL: String?,
    @Json(name = "VODITEL")
    val VODITEL: String?
)