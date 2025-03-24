package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.ErrorDataDTO
import com.vodovoz.app.domain.general.model.ErrorDataModel


fun ErrorDataDTO.toDomain(): ErrorDataModel{
    return ErrorDataModel(
        titleHtml = title ?: "",
        descriptionHtml = message ?: "",
        imageUrl = imageUrl?.toFullUrl() ?: ""
    )
}