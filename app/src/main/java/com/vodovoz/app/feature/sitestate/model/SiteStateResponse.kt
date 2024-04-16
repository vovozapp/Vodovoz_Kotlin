package com.vodovoz.app.feature.sitestate.model

data class SiteStateResponse(
    val active: String?,
    val data: SiteStateData?,
    val requestUrl: String?,
    val showComments: Boolean?,
    val generation: Generation?,
)

data class SiteStateData(
    val title: String?,
    val logo: String?,
    val desc: String?,
    val email: String?,
    val whatsUp: SiteStateContact?,
    val viber: SiteStateContact?,
    val telegram: SiteStateContact?,
    val chat: SiteStateContact?,
    val phone: SiteStateContact?,
    val time: String?,
)

data class SiteStateContact(
    val url: String,
    val image: String,
)

data class Generation(
    val enabled: Boolean,
    val time: String,
)