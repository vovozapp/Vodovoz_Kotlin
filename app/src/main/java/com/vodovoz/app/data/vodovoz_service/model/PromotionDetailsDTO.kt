package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class PromotionDetailsDTO(
    @Json(name = "AKCIYA")
    val AKCIYA: AKCIYA_DTO?,
    @Json(name = "TOVAR")
    val TOVAR: TOVAR_DTO?
)