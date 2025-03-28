package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.login.KNOPKA_AUTH_DTO
import com.vodovoz.app.data.vodovoz_service.model.login.LoginDetailsDTO
import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.login.LoginDetailsModel

fun LoginDetailsDTO.toDomain(): LoginDetailsModel {
    return LoginDetailsModel(
        title = TITLE ?: "",
        description = OPISANIE ?: "",
        fields = DATA?.mapToDomain()
            ?: throw IllegalArgumentException("Login fields can't be null"),
        hasAgreement = SOGLASHENIE == "Y",
        navigationButton = KNOPKA_AUTH?.toDomain() ?: throw IllegalArgumentException("Navigation button can't be null"),
        mainButton =KNOPKA?.toDomain() ?: throw IllegalArgumentException("Login button can't be null")
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