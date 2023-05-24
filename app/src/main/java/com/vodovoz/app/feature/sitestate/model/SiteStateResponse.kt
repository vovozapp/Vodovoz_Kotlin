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
    val requestPhone: String?,
    @Json(name = "COMMENTFILES")
    val showComments: Boolean?
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
    val whatsUp: SiteStateWhatsUp?,
    @Json(name = "VIBER")
    val viber: SiteStateViber?,
    @Json(name = "TELEGA")
    val telegram: SiteStateTelegram?,
    @Json(name = "JIVOSAIT")
    val chat: SiteStateChat?,
    @Json(name = "PHONE")
    val phone: SiteStatePhone?,
    @Json(name = "TIME")
    val time: String?
)

@JsonClass(generateAdapter = true)
data class SiteStatePhone(
    @Json(name = "URL")
    val url: String,
    @Json(name = "IMAGES")
    val image: String
)

@JsonClass(generateAdapter = true)
data class SiteStateWhatsUp(
    @Json(name = "URL")
    val url: String,
    @Json(name = "IMAGES")
    val image: String
)

@JsonClass(generateAdapter = true)
data class SiteStateViber(
    @Json(name = "URL")
    val url: String,
    @Json(name = "IMAGES")
    val image: String
)

@JsonClass(generateAdapter = true)
data class SiteStateTelegram(
    @Json(name = "URL")
    val url: String,
    @Json(name = "IMAGES")
    val image: String
)

@JsonClass(generateAdapter = true)
data class SiteStateChat(
    @Json(name = "URL")
    val url: String,
    @Json(name = "IMAGES")
    val image: String
)