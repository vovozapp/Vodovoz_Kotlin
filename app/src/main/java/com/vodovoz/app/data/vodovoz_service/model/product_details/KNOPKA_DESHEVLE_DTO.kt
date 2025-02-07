package com.vodovoz.app.data.vodovoz_service.model.product_details


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class KNOPKA_DESHEVLE_DTO(
    @Json(name = "BACKGROUND")
    val BACKGROUND: String?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "TEXTCOLOR")
    val TEXTCOLOR: String?,
)
