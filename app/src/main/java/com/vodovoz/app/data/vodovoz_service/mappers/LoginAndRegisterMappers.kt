package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.REGISTRATION_FIELD_DTO
import com.vodovoz.app.data.vodovoz_service.model.RegistrationDetailsDTO
import com.vodovoz.app.domain.general.model.FieldModel
import com.vodovoz.app.domain.general.model.login.AuthDetailsModel


fun RegistrationDetailsDTO.toDomain(): AuthDetailsModel {
    return AuthDetailsModel(
        title = TITLE ?: "",
        description = "",
        fields = DATA?.mapToDomain()
            ?: throw IllegalArgumentException("Login fields can't be null"),
        hasAgreement = SOGLASHENIE == "Y",
        navigationButton = KNOPKA_AUTH?.toDomain()
            ?: throw IllegalArgumentException("Navigation button can't be null"),
        mainButton = KNOPKA?.toDomain()
            ?: throw IllegalArgumentException("Login button can't be null")
    )
}

fun List<REGISTRATION_FIELD_DTO>.mapToDomain(): List<FieldModel> {
    return mapNotNull { it.toDomain() }
}

fun REGISTRATION_FIELD_DTO.toDomain(): FieldModel? {
    return FieldModel(
        id = CODE ?: return null,
        label = TEXT ?: "",
        value = "",
        valueType = POLE ?: "text",
        isRequired = OBYZATELNO == "Y",
        readOnly = false,
        supportingText = "",
        hint = TEXT_V_POLE ?: ""
    )
}