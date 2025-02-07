package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class BLOCK_RAZDEL_INFO_DTO(
    @Json(name = "DATA")
    val DATA: BLOCK_RAZDEL_INFO_DATA_DTO?,
    @Json(name = "TITLE")
    val TITLE: String?
)