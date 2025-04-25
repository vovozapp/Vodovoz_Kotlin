package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.auth.KNOPKA_AUTH_DTO
import com.vodovoz.app.data.vodovoz_service.model.auth.LoginDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.auth.UserAuthInfoDTO
import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.login.AuthDetailsModel
import com.vodovoz.app.domain.general.model.login.UserAuthInfoModel

fun LoginDetailsDTO.toDomain(): AuthDetailsModel {
    return AuthDetailsModel(
        title = TITLE ?: "",
        description = OPISANIE ?: "",
        fields = DATA?.mapToDomain()
            ?: throw IllegalArgumentException("Login fields can't be null"),
        haveAgreement = SOGLASHENIE == "Y",
        buttons = KNOPKA?.map { it.toDomain() }
            ?: throw IllegalArgumentException("Auth button can't be null")
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

fun UserAuthInfoDTO.toDomain(): UserAuthInfoModel {
    return UserAuthInfoModel(
        userId = userId ?: userId2 ?: throw IllegalArgumentException("userId is required"),
        authStatus = authStatus ?: true,
        token = token ?: throw IllegalArgumentException("token is required")
    )
}