package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class DETAILTEXT_DTO(
    @Json(name = "DATAID")
    val DATAID: String?,
    @Json(name = "TITLE")
    val TITLE: String?
)