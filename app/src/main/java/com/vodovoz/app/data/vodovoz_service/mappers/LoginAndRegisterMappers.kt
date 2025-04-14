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
            ?: throw IllegalArgumentException("Register fields can't be null"),
        haveAgreement = SOGLASHENIE == "Y",
        buttons = KNOPKA?.map { it.toDomain() } ?: throw IllegalArgumentException("Register buttons can't be null")
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