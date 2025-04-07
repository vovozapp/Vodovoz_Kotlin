package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.BannerDTO
import com.vodovoz.app.domain.general.model.BannerModel


fun List<BannerDTO>.mapToDomain(): List<BannerModel> {
    return mapNotNull { it.toDomain() }
}

fun BannerDTO.toDomain(): BannerModel? {
    return BannerModel(
        id = ID ?: return null,
        name = NAME ?: "",
        detailPicture = DETAIL_PICTURE?.toFullUrl() ?: return null,
        action = HARAKTERISTIK?.toAction(IBLOCK_ID ?: 73L) ?: return null,
        advertising = OREKLAME?.toDomain()
    )
}