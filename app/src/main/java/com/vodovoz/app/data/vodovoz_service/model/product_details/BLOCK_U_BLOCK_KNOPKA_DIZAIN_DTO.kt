package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class BLOCK_U_BLOCK_KNOPKA_DIZAIN_DTO(
    @Json(name = "BACKGROUND")
    val BACKGROUND: String?,
    @Json(name = "BORDER_COLOR")
    val BORDER_COLOR: String?,
    @Json(name = "KARTINKA")
    val KARTINKA: String?,
    @Json(name = "KNOPKA")
    val KNOPKA: BLOCK_KNOPKA_VALUE_DTO?,
    @Json(name = "TEXCOLOR")
    val TEXTCOLOR: String?,
    @Json(name = "TITLE")
    val TITLE: String?
)