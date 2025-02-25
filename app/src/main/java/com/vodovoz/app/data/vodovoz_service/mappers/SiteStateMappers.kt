package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.CHATJIVO_DTO
import com.vodovoz.app.data.vodovoz_service.model.GENERATION_DTO
import com.vodovoz.app.data.vodovoz_service.model.SOGLASHENIE_DTO
import com.vodovoz.app.data.vodovoz_service.model.SiteStateResponseDTO
import com.vodovoz.app.domain.general.model.AgreementModel
import com.vodovoz.app.domain.general.model.JivoChatModel
import com.vodovoz.app.domain.general.model.SiteStateModel
import com.vodovoz.app.domain.general.model.TrackingConfig

fun SiteStateResponseDTO.toDomain(): SiteStateModel {
    return SiteStateModel(
        isActive = ACTIVE?.toBoolean() ?: false,
        url = TESTSAITSSILKA ?: "",
        smsUrl = SMSRASSILKA ?: "",
        isSmsEnabled = REGISTRACION_SMS == "Y",
        jivoChat = CHATJIVO?.toJivoChatModel() ?: JivoChatModel(isActive = false, url = ""),
        tracking = GENERATION?.toTrackingConfig() ?: TrackingConfig(trackingIsEnabled = false, time = 0),
        agreement = SOGLASHENIE?.toAgreementModel() ?: throw IllegalArgumentException("Agreement can't be null"),
        showComments = COMMENTFILES ?: false
    )
}


fun CHATJIVO_DTO.toJivoChatModel(): JivoChatModel {
    return JivoChatModel(
        isActive = ACTIVE == "Y" && SSILKA != null,
        url = SSILKA ?: ""
    )
}

fun GENERATION_DTO.toTrackingConfig(): TrackingConfig {
    return TrackingConfig(
        trackingIsEnabled = TRAKING == "Y",
        time = TIME?.toInt() ?: 30
    )
}

fun SOGLASHENIE_DTO.toAgreementModel(): AgreementModel {
    return AgreementModel(
        htmlText = TEXT ?: "",
        titles = ZAGOLOVOKi?.mapNotNull { it } ?: emptyList()
    )
}