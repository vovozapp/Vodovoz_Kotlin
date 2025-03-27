package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class VodovozErrorResponseDTO(
    @Json(name = "message")
    val message: String?,
    @Json(name = "status")
    val status: String?,
    @Json(name = "title")
    val title: String?
)