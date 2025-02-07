package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class COMMENT_DTO(
    @Json(name = "DATA")
    val DATA: String?,
    @Json(name = "KYPLEN")
    val KYPLEN: String?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "RATING")
    val RATING: Int?,
    @Json(name = "TEXT")
    val TEXT: String?,
    @Json(name = "USER_PHOTO")
    val USER_PHOTO: String?
)