package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class DOCUMENTS_DTO(
    @Json(name = "DATA")
    val DATA: List<DOCUMENT_DTO>?,
    @Json(name = "ID")
    val ID: String?,
    @Json(name = "TITLE")
    val TITLE: String?
)