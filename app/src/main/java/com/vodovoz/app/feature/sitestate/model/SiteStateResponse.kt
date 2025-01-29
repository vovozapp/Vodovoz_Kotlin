package com.vodovoz.app.feature.sitestate.model

data class SiteStateResponse(
    val active: String?,
    val data: SiteStateData?,
    val requestUrl: String?,
    val showComments: Boolean?,
    val generation: Generation?,
    val agreement: Agreement?,
    val jivoChat: JivoChat?,
    val testSiteLink: String?
)

data class JivoChat (
    val active: Boolean?,
    val url: String?,
)

data class Agreement (
    val text: String,
    val titles: List<String>?,
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