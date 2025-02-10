package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class HARAKTERISTIKI_DTO(
    @Json(name = "BINDS")
    val BINDS: List<HARAKTERISTIK_BIND_DTO?>?,
    @Json(name = "CODE")
    val CODE: String?,
    @Json(name = "ICON_PATH")
    val ICON_PATH: String?,
    @Json(name = "ID")
    val ID: Int?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "SORT")
    val SORT: String?,
    @Json(name = "XML_ID")
    val XML_ID: String?
)