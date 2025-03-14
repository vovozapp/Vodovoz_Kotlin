package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.unrated_products.UnratedProductDTO
import com.vodovoz.app.data.vodovoz_service.model.unrated_products.UnratedProductsSectionDTO
import com.vodovoz.app.domain.general.model.EmptyResultException
import com.vodovoz.app.domain.general.model.UnratedProductModel
import com.vodovoz.app.domain.general.model.UnratedProductsSectionModel

fun UnratedProductsSectionDTO.toDomain(): UnratedProductsSectionModel {
    return UnratedProductsSectionModel(
        title = TITLERAZDEL ?: "",
        products = LISTRAZDEL?.mapNotNull { it.toDomain() }
            ?: throw EmptyResultException("Unrated products are empty"),
        productTitle = TITLETOVAR ?: "",
        countProductsText = VSEGOTOVAR ?: ""
    )
}

fun UnratedProductDTO.toDomain(): UnratedProductModel? {
    return UnratedProductModel(
        id = ID ?: return null,
        name = NAME ?: return null,
        detailPicture = DETAIL_PICTURE ?: return null
    )
}