package com.vodovoz.app.data.vodovoz_service.model.auth

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class UserAuthInfoDTO(
    @Json(name = "user_id")
    val userId: Long?,
    @Json(name = "userid")
    val userId2: Long?,
    @Json(name = "auth_status")
    val authStatus: Boolean?,
    @Json(name = "token")
    val token: String?
)
