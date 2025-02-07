package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class DetailPageUrlIos(
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "URL")
    val URL: String?
)