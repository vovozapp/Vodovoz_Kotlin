package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.BrandDTO
import com.vodovoz.app.data.vodovoz_service.model.BrandSectionDTO
import com.vodovoz.app.domain.general.model.BrandModel
import com.vodovoz.app.domain.general.model.BrandSectionModel

fun BrandSectionDTO.toDomain(): BrandSectionModel{
    return BrandSectionModel(
        title = TITLE ?: "",
        count = COUNT ?: "",
        brands = DATA?.mapToDomain() ?: emptyList()
    )
}

fun List<BrandDTO>.mapToDomain(): List<BrandModel>{
    return mapNotNull { it.toDomain() }
}

fun BrandDTO.toDomain(): BrandModel? {
    return BrandModel(
        id = ID ?: return null,
        name = NAME ?: return null,
        picture = DETAIL_PICTURE?.toFullUrl() ?: "",
        pageUrl = DETAIL_PAGE_URL ?: ""
    )
}