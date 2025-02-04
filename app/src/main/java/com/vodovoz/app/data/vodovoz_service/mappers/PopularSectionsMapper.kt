package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toVodovozImage
import com.vodovoz.app.data.vodovoz_service.model.CATEGORY_DTO
import com.vodovoz.app.data.vodovoz_service.model.PopularCategoriesDTO
import com.vodovoz.app.domain.general.model.PopularCategoryModel
import com.vodovoz.app.domain.general.model.SectionModel

fun PopularCategoriesDTO.mapToDomain(): SectionModel<PopularCategoryModel> {
    return SectionModel(
        title = TITLERAZDEL ?: "",
        items = LISTRAZDEL?.mapNotNull { it?.mapToDomain() } ?: emptyList(),
        button = null
    )
}

fun CATEGORY_DTO.mapToDomain(): PopularCategoryModel? {
    return PopularCategoryModel(
        id = this.IDRAZDEL?.toLong() ?: return null,
        name = this.NAMERAZDEL ?: return null,
        picture = this.PICTURE?.toVodovozImage() ?: return null
    )
}