package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.BannerDTO
import com.vodovoz.app.domain.general.model.BannerModel


fun List<BannerDTO>.toDomain(): List<BannerModel> {
    return mapNotNull { it.toDomain() }
}

fun BannerDTO.toDomain(): BannerModel? {
    return BannerModel(
        id = ID ?: return null,
        name = NAME ?: "",
        detailPicture = DETAIL_PICTURE ?: return null,
        action = HARAKTERISTIK?.toAction() ?: return null,
        advertising = OREKLAME?.toDomain()
    )
}