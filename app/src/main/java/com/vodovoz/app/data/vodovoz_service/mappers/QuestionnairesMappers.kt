package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.QuestionnairesDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.QuestionnairesItemDTO
import com.vodovoz.app.data.vodovoz_service.model.QuestionnairesWelcomeDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.USLOVIE_DTO
import com.vodovoz.app.domain.general.model.ConditionModel
import com.vodovoz.app.domain.general.model.QuestionnairesDetailsModel
import com.vodovoz.app.domain.general.model.QuestionnairesItemModel
import com.vodovoz.app.domain.general.model.QuestionnairesWelcomeDetailsModel

fun QuestionnairesWelcomeDetailsDTO.toDomain(): QuestionnairesWelcomeDetailsModel {
    return QuestionnairesWelcomeDetailsModel(
        title = TITLE ?: "",
        image = KARTINKA?.toFullUrl() ?: "",
        header = ZAGOLOVOK ?: "",
        description = OPISANIE ?: "",
        buttons = KNOPKA?.map { it.toDomain() }
            ?: throw IllegalArgumentException("QuestionnairesWelcomeDetails buttons can't be null")
    )

}

fun QuestionnairesDetailsDTO.toDomain(): QuestionnairesDetailsModel {
    return QuestionnairesDetailsModel(
        title = TITLE ?: "",
        items = DATA?.map { it.toDomain() }
            ?: throw IllegalArgumentException("QuestionnairesDetailsDTO can't be empty"),
        button = KNOPKA?.toDomain()
            ?: throw IllegalArgumentException("QuestionnairesDetailsDTO button can't be empty")
    )
}

fun QuestionnairesItemDTO.toDomain(): QuestionnairesItemModel {
    return QuestionnairesItemModel(
        name = TEXT ?: "",
        hint = TEXT_V_POLE ?: "",
        code = CODE ?: "",
        type = POLE ?: "",
        required = OBYAZATELNO == "Y",
        multiple = MULTIPLE == "Y",
        value = VALUE ?: "",
        values = RAZDEL ?: emptyList(),
        conditions = USLOVIE?.map { it.toDomain() } ?: emptyList()
    )
}

fun USLOVIE_DTO.toDomain(): ConditionModel {
    return ConditionModel(
        id = ID ?: "",
        text = TEXT ?: "",
        url = URL ?: ""
    )
}