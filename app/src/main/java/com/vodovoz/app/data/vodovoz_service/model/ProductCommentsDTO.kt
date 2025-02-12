package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ProductCommentsDTO(
    @Json(name = "COMMENT_COUNT")
    val COMMENT_COUNT: Int?,
    @Json(name = "COMMENT_COUNT_TEXT")
    val COMMENT_COUNT_TEXT: String?,
    @Json(name = "IMAGES")
    val IMAGES: List<String>?,
    @Json(name = "COMMENTS")
    val COMMENTS: List<COMMENT_DTO?>?,
    @Json(name = "RAITINGOSNOVA")
    val RAITINGOSNOVA: String?,
    @Json(name = "SORTIROVKA")
    val SORTIROVKA: List<SORT_DTO?>?
)