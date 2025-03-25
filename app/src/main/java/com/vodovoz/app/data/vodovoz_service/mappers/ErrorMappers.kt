package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.ErrorDataButtonDTO
import com.vodovoz.app.data.vodovoz_service.model.ErrorDataDTO
import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.ErrorDataModel


fun ErrorDataDTO.toDomain(): ErrorDataModel {
    return ErrorDataModel(
        headerHtml = header ?: "",
        descriptionHtml = message ?: "",
        imageUrl = imageUrl?.toFullUrl() ?: "",
        title = title ?: "",
        button = button?.toDomain()
    )
}

fun ErrorDataButtonDTO.toDomain(): ColorfulButtonModel? {
    return ColorfulButtonModel(
        name = text ?: "",
        backgroundColor = background ?: "",
        textColor = color ?: "",
        id = id ?: ""
    )
}
