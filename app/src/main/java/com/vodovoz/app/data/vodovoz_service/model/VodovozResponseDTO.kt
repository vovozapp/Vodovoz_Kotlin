package com.vodovoz.app.data.vodovoz_service.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VodovozResponseDTO<T>(
    @Json(name = "status")
    val status: String?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "data")
    val data: T?,
    val error: ErrorDataDTO?
)

@JsonClass(generateAdapter = true)
data class ErrorDataDTO(
    @Json(name = "ZAGALOVOK") val title: String,
    @Json(name = "MESSAGE") val message: String,
    @Json(name = "IMAGE") val imageUrl: String,
)