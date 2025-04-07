package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.CATEGORY_NODE_DTO
import com.vodovoz.app.domain.general.model.ParentCategoryModel


fun List<CATEGORY_NODE_DTO>.mapToDomain(): List<ParentCategoryModel>{
    return mapNotNull { it.toDomain() }
}

fun CATEGORY_NODE_DTO.toDomain(): ParentCategoryModel? {
    val subCategories = PODRAZDEL?.mapNotNull { it.toDomain() } ?: emptyList()
    return ParentCategoryModel(
        id = ID ?: return null,
        name = NAME ?: return null,
        picture = "",
        action = null,
        parentId = null,
        depthLevel = DEPTH_LEVEL ?: 1,
        subcategoriesQuantity = subCategories.size,
        childCategories = subCategories
    )
}