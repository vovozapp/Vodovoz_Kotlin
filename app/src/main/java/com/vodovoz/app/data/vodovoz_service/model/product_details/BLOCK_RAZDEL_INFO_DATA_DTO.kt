package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class BLOCK_RAZDEL_INFO_DATA_DTO(
    @Json(name = "DETAIL_PICTURE")
    val DETAIL_PICTURE: String?,
    @Json(name = "ID")
    val ID: Int?,
    @Json(name = "NAME")
    val NAME: String?
)