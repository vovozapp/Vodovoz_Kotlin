package com.vodovoz.app.data.vodovoz_service.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class BrandDTO(
    @Json(name = "ID") val ID: Long?,
    @Json(name = "NAME") val NAME: String?,
    @Json(name = "DETAIL_PICTURE") val DETAIL_PICTURE: String?,
    @Json(name = "DETAIL_PAGE_URL") val DETAIL_PAGE_URL: String?
)