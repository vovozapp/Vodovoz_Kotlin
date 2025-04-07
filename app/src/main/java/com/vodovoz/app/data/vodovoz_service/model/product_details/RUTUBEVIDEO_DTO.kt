package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class RUTUBEVIDEO_DTO(
    @Json(name = "IMAGE")
    val IMAGE: String?,
    @Json(name = "VIDEO")
    val VIDEO: String?
)