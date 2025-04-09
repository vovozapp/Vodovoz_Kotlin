package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.login.KNOPKA_AUTH_DTO
import com.vodovoz.app.data.vodovoz_service.model.login.LoginDetailsDTO
import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.login.AuthDetailsModel

fun LoginDetailsDTO.toDomain(): AuthDetailsModel {
    return AuthDetailsModel(
        title = TITLE ?: "",
        description = OPISANIE ?: "",
        fields = DATA?.mapToDomain()
            ?: throw IllegalArgumentException("Login fields can't be null"),
        hasAgreement = SOGLASHENIE == "Y",
        buttons = KNOPKA?.map { it.toDomain() } ?: throw IllegalArgumentException("Auth button can't be null")
    )
}

fun KNOPKA_AUTH_DTO.toDomain(): ColorfulButtonModel {
    return ColorfulButtonModel(
        name = TITLE ?: "",
        backgroundColor = BACKGROUND ?: "",
        textColor = TEXTCOLOR ?: "",
        id = ID ?: ""
    )
}