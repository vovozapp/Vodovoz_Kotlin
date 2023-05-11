package com.vodovoz.app.feature.sitestate.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SiteStateResponse(
    @Json(name = "ACTIVE")
    val active: String?,
    @Json(name = "DATA")
    val data: SiteStateData?,
    @Json(name = "SMSRASSILKA")
    val requestPhone: String?
)

@JsonClass(generateAdapter = true)
data class SiteStateData(
    @Json(name = "TITLE")
    val title: String?,
    @Json(name = "LOGO")
    val logo: String?,
    @Json(name = "OPISANIE")
    val desc: String?,
    @Json(name = "EMAIL")
    val email: String?,
    @Json(name = "WATSAP")
    val whatsUp: String?,
    @Json(name = "VIBER")
    val viber: String?,
    @Json(name = "TELEGA")
    val telegragm: String?,
    @Json(name = "JIVOSAIT")
    val chat: String?,
    @Json(name = "PHONE")
    val phone: String?,
    @Json(name = "TIME")
    val time: String?
)