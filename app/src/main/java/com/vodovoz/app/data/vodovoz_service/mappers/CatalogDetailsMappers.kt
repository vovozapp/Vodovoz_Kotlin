package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.catalog_details.CATALOG_CATEGORY_DTO
import com.vodovoz.app.data.vodovoz_service.model.catalog_details.CatalogDetailsDTO
import com.vodovoz.app.domain.general.model.CatalogCategoryModel
import com.vodovoz.app.domain.general.model.CatalogDetailsModel

fun CatalogDetailsDTO.toDomain(): CatalogDetailsModel {
    return CatalogDetailsModel(
        banners = BANNER?.mapNotNull { it?.toDomain() } ?: emptyList(),
        categories = RAZDEL?.mapToDomain() ?: emptyList()
    )
}

fun CATALOG_CATEGORY_DTO.toDomain(): CatalogCategoryModel? {
    return CatalogCategoryModel(
        id = ID ?: return null,
        name = NAME ?: "",
        picture = PICTURE?.toFullUrl() ?: return null,
        action = UF_SILKAPEREXOD?.toDataAllAction(),
        parentId = IBLOCK_SECTION_ID,
        depthLevel = DEPTH_LEVEL ?: 1,
        subcategoriesQuantity = SUBSECTIONS ?: 0,
        childCategories = PODRAZDEL?.mapToDomain() ?: emptyList()
    )
}

fun List<CATALOG_CATEGORY_DTO?>.mapToDomain(): List<CatalogCategoryModel> {
    return mapNotNull { category -> category?.toDomain() }
}