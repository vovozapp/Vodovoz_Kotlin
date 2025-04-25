package com.vodovoz.app.domain.general.model

data class SiteState(
    val isActive: Boolean,
    val testUrl: String,
    val smsUrl: String,
    val isSmsEnabled: Boolean,
    val showComments: Boolean,
    val jivoChat: JivoChatModel,
    val tracking: TrackingConfig,
    val agreement: AgreementModel,
    val data: SiteStateDataModel? = null
)

data class JivoChatModel(
    val isActive: Boolean,
    val url: String,
)

data class AgreementModel(
    val html: String,
    val titles: List<String>,
)

data class TrackingConfig(
    val trackingIsEnabled: Boolean,
    val time: Int,
)

data class SiteStateDataModel(
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
