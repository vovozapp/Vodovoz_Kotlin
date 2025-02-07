package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class BLOCK_RAZDEL_DTO(
    @Json(name = "BRAND")
    val BRAND: BLOCK_RAZDEL_INFO_DTO?,
    @Json(name = "RAZDEL")
    val RAZDEL: BLOCK_RAZDEL_INFO_DTO?
)