package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ZALOG_OPISANIE_DTO(
    @Json(name = "DOPOPISANIE")
    val DOPOPISANIE: String?,
    @Json(name = "TEXT")
    val TEXT: String?
)