package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toVodovozImage
import com.vodovoz.app.data.vodovoz_service.model.CATEGORY_DTO
import com.vodovoz.app.data.vodovoz_service.model.PopularCategoriesDTO
import com.vodovoz.app.domain.general.model.PopularCategoryModel

fun PopularCategoriesDTO.mapToDomain(): List<PopularCategoryModel>{
    return LISTRAZDEL?.mapNotNull { it?.mapToDomain() } ?: emptyList()
}

fun CATEGORY_DTO.mapToDomain(): PopularCategoryModel? {
    return PopularCategoryModel(
        id = this.IDRAZDEL?.toLong() ?: return null,
        name = this.NAMERAZDEL ?: return null,
        picture = this.PICTURE?.toVodovozImage() ?: return null
    )
}