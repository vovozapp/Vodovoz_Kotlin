package com.vodovoz.app.data.vodovoz_service.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class ErrorMessageResponseDTO(
    @Json(name = "status")
    val status: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "message")
    val message: String
)
