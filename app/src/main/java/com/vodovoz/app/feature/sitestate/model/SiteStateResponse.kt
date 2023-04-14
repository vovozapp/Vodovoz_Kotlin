package com.vodovoz.app.feature.sitestate.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SiteStateResponse(
    @Json(name = "ACTIVE")
    val active: String?,
    @Json(name = "DATA")
    val data: Any?,
    @Json(name = "SMSRASSILKA")
    val requestPhone: String?
)