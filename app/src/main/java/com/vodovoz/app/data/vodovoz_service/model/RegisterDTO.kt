package com.vodovoz.app.data.vodovoz_service.model

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class RegisterDTO(
    @Json(name = "status")
    val status: String?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "userid")
    val userId: Long?
)