package com.vodovoz.app.data.vodovoz_service.model

import com.squareup.moshi.Json

data class VodovozResponseDTO<T>(
    @Json(name = "status")
    val status: String?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "data")
    val data: T?
)

data class VodovozResponseV2DTO<T>(
    @Json(name = "status")
    val status: String?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "DATA")
    val data: T?
)
