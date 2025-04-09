package com.vodovoz.app.data.vodovoz_service.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.vodovoz.app.domain.general.model.ColorfulButtonModel

@JsonClass(generateAdapter = true)
@Keep
data class VodovozResponseDTO<T>(
    @Json(name = "status")
    val status: String?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "data")
    val data: T?,
    @Json(name = "errordata")
    val error: ErrorDataDTO?,
)

@JsonClass(generateAdapter = true)
@Keep
data class ErrorDataDTO(
    @Json(name = "TITLE") val title: String?,
    @Json(name = "ZAGALOVOK") val header: String?,
    @Json(name = "MESSAGE") val message: String?,
    @Json(name = "IMAGE") val imageUrl: String?,
    @Json(name = "KNOPKA") val button: ErrorDataButtonDTO?,
)

@JsonClass(generateAdapter = true)
@Keep
data class ErrorDataButtonDTO(
    @Json(name = "TEXT")
    val text: String?,
    @Json(name = "COLOR")
    val color: String?,
    @Json(name = "BACKGROUND")
    val background: String?,
    @Json(name = "ID")
    val id: String?,
)