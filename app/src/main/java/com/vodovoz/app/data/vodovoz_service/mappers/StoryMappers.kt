package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toVodovozImage
import com.vodovoz.app.data.vodovoz_service.model.COLORFUL_KNOPKA_DTO
import com.vodovoz.app.data.vodovoz_service.model.HARAKTERISTIK_DTO
import com.vodovoz.app.data.vodovoz_service.model.STORY_DTO
import com.vodovoz.app.data.vodovoz_service.model.VNYTRENNOST_DTO
import com.vodovoz.app.data.vodovoz_service.model.StoriesDTO
import com.vodovoz.app.domain.general.model.ActionWithButtonModel
import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.StoryModel


fun StoriesDTO.toDomain(): List<StoryModel> {
    return data?.mapNotNull { it.toDomain() } ?: emptyList()
}

fun STORY_DTO.toDomain(): StoryModel? {
    return StoryModel(
        id = ID ?: return null,
        image = RAZDEL?.IMAGE?.toVodovozImage() ?: "",
        actionWithButtonList = VNYTRENNOST?.mapNotNull { it?.toDomain() } ?: return null,
        viewed = false
    )
}

fun VNYTRENNOST_DTO.toDomain(): ActionWithButtonModel? {
    return ActionWithButtonModel(
        action = HARAKTERISTIK_DTO(ACTION, ID).toAction() ?: return null,
        colorfulButton = KNOPKA?.toDomain() ?: return null
    )
}

fun COLORFUL_KNOPKA_DTO.toDomain(): ColorfulButtonModel? {
    return ColorfulButtonModel(
        name = NAME ?: return null,
        backgroundColor = COLOR_BACKGROUND ?: return null,
        textColor = COLOR_TEXT ?: return null
    )
}