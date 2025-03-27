package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.REGISTRATION_FIELD_DTO
import com.vodovoz.app.data.vodovoz_service.model.RegistrationSectionDTO
import com.vodovoz.app.domain.general.model.FieldModel
import com.vodovoz.app.domain.general.model.SectionModel


fun RegistrationSectionDTO.toDomain(): SectionModel<FieldModel>{
    return SectionModel(
        title = TITLE ?: "",
        items = DATA?.mapToDomain() ?: emptyList(),
        button = null
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