package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.VodovozButtonDTO
import com.vodovoz.app.data.vodovoz_service.model.VodovozPlaceholderDTO
import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.VodovozPlaceholderModel


fun VodovozPlaceholderDTO.toDomain(): VodovozPlaceholderModel {
    return VodovozPlaceholderModel(
        headerHtml = header ?: "",
        descriptionHtml = message ?: "",
        imageUrl = imageUrl?.toFullUrl() ?: "",
        title = title ?: "",
        button = button?.toDomain()
    )
}

fun VodovozButtonDTO.toDomain(): ColorfulButtonModel {
    return ColorfulButtonModel(
        name = text ?: "",
        backgroundColor = background ?: "",
        textColor = color ?: "",
        id = id ?: ""
    )
}
