package com.vodovoz.app.data.model.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserReloginEntity(
    @Json(name = "data")
    val isAuthorized: Boolean
)
