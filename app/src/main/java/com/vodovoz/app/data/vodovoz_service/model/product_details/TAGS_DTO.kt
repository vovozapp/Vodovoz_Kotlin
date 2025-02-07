package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class TAGS_DTO(
    @Json(name = "TAGS")
    val TAGS: List<String?>?,
    @Json(name = "TITLE")
    val TITLE: String?
)