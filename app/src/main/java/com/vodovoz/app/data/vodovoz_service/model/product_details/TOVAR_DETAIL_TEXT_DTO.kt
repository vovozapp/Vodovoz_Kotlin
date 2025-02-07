package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class TOVAR_DETAIL_TEXT_DTO(
    @Json(name = "ID")
    val ID: String?,
    @Json(name = "OPISANIE")
    val OPISANIE: String?,
    @Json(name = "TITLE")
    val TITLE: String?
)