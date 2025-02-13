package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.COMMENT_DTO

@Keep
data class COMMENTS_DTO(
    @Json(name = "COMENTS")
    val COMMENTS: List<COMMENT_DTO?>?,
    @Json(name = "COMMENT_COUNT")
    val COMMEN_COUNT: Int?,
    @Json(name = "COMMENT_COUNT_TEXT")
    val COMMENT_COUNT_TEXT: String?,
    @Json(name = "KARTINKI")
    val KARTINKI: String?
)