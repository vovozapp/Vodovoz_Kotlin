package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class BLOCTOVAR_DTO(
    @Json(name = "AKSESSYAR")
    val AKSESSYAR: TOVAR_SECTION_DTO?,
    @Json(name = "POHOSHIE")
    val POHOSHIE: TOVAR_SECTION_DTO?
)