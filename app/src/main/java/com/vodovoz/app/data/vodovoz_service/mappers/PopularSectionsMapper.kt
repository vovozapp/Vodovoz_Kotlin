package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.CATEGORY_DTO
import com.vodovoz.app.data.vodovoz_service.model.PopularCategoriesDTO
import com.vodovoz.app.domain.general.model.PopularCategoryModel
import com.vodovoz.app.domain.general.model.SectionModel

fun PopularCategoriesDTO.toDomain(): SectionModel<PopularCategoryModel> {
    return SectionModel(
        title = TITLERAZDEL ?: "",
        items = LISTRAZDEL?.mapNotNull { it?.toDomain() } ?: emptyList(),
        button = null
    )
}

fun CATEGORY_DTO.toDomain(): PopularCategoryModel? {
    return PopularCategoryModel(
        id = this.IDRAZDEL?.toLong() ?: return null,
        name = this.NAMERAZDEL ?: return null,
        picture = this.PICTURE?.toFullUrl() ?: return null
    )
}