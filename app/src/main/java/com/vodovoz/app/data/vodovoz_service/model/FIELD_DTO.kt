package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class FIELD_DTO(
    @Json(name = "COMMENTS")
    val COMMENTS: String?,
    @Json(name = "REQUIRED")
    val REQUIRED: String?,
    @Json(name = "SID")
    val SID: String?,
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "TITLE_TYPE")
    val TITLE_TYPE: String?,
    @Json(name = "VALUE")
    val VALUE: String?,
    @Json(name = "ZAPRETREDAKTOR")
    val ZAPRETREDAKTOR: String?
)