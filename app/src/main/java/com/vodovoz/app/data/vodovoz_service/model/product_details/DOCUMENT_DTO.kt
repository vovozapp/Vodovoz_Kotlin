package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class DOCUMENT_DTO(
    @Json(name = "DESCRIPTION")
    val DESCRIPTION: String?,
    @Json(name = "FILE_SIZE")
    val FILE_SIZE: Double?,
    @Json(name = "FILE_SIZE_FORMAT")
    val FILE_SIZE_FORMAT: String?,
    @Json(name = "IKONKA")
    val IKONKA: String?,
    @Json(name = "SRC")
    val SRC: String?,
    @Json(name = "TYPE")
    val TYPE: String?
)