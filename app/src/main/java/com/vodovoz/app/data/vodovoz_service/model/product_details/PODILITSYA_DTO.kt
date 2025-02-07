package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class PODILITSYA_DTO(
    @Json(name = "detail_page_url")
    val detail_page_url: String?,
    @Json(name = "detail_page_url_ios")
    val detail_page_url_ios: DetailPageUrlIos?
)