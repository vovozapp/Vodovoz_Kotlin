package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.catalog.CATALOG_CATEGORY_DTO
import com.vodovoz.app.data.vodovoz_service.model.catalog.CatalogDetailsDTO
import com.vodovoz.app.domain.general.model.ParentCategoryModel
import com.vodovoz.app.domain.general.model.CatalogDetailsModel

fun CatalogDetailsDTO.toDomain(): CatalogDetailsModel {
    return CatalogDetailsModel(
        banners = BANNER?.mapNotNull { it?.toDomain() } ?: emptyList(),
        categories = RAZDEL?.mapToDomain() ?: emptyList()
    )
}

fun CATALOG_CATEGORY_DTO.toDomain(): ParentCategoryModel? {
    return ParentCategoryModel(
        id = ID ?: return null,
        name = NAME ?: "",
        picture = PICTURE?.toFullUrl() ?: "",
        action = UF_SILKAPEREXOD?.toDataAllAction(),
        parentId = IBLOCK_SECTION_ID,
        depthLevel = DEPTH_LEVEL ?: 1,
        childCategories = PODRAZDEL?.mapToDomain() ?: emptyList(),
        countChildren = SUBSECTIONS ?: 0
    )
}

fun List<CATALOG_CATEGORY_DTO?>.mapToDomain(): List<ParentCategoryModel> {
    return mapNotNull { category -> category?.toDomain() }
}