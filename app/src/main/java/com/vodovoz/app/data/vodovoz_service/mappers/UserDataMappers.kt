package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.user_data.FOTO_DTO
import com.vodovoz.app.data.vodovoz_service.model.user_data.POLE_DTO
import com.vodovoz.app.data.vodovoz_service.model.user_data.UserDataDTO
import com.vodovoz.app.domain.general.model.FieldModel
import com.vodovoz.app.domain.general.model.UserDataModel
import com.vodovoz.app.domain.general.model.UserDataPhotoModel

fun UserDataDTO.toDomain(): UserDataModel {
    return UserDataModel(
        title = TITLE ?: "",
        photo = FOTO?.toDomain() ?: throw IllegalArgumentException("UserDataPhoto can't be null"),
        fields = POLYA?.mapToDomain()
            ?: throw IllegalArgumentException("UserDataFields can't be null")
    )
}


fun FOTO_DTO.toDomain(): UserDataPhotoModel {
    return UserDataPhotoModel(
        imageUrl = IMG?.toFullUrl() ?: "",
        title = TITLE ?: "",
        description = OPISANIE ?: ""
    )
}

fun List<POLE_DTO>.mapToDomain(): List<FieldModel> {
    return mapNotNull { it.toDomain() }
}

fun POLE_DTO.toDomain(): FieldModel? {
    return FieldModel(
        id = CODE ?: return null,
        label = NAME ?: TEXT ?: "",
        value = VALUE ?: "",
        valueType = POLE ?: "text",
        isRequired = (OBYZATELNO ?: OBAZATELEN) == "Y",
        readOnly = ZABLOCKPOLE == "Y",
        supportingText = TEXT ?: OPIS ?: "",
        hint = TEXTOPIS ?: TEXT_V_POLE ?: TEXTVPOLE ?:  "",
    )
}