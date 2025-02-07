package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class INFORMATIONS_DTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "ZNACHENIE")
    val ZNACHENIE: String?
)