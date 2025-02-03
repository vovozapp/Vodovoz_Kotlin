package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.CATEGORY_WITH_PRODUCTS_DTO
import com.vodovoz.app.data.vodovoz_service.model.RAZDEL_VERH_NIH
import com.vodovoz.app.data.vodovoz_service.model.SuperTopAndBottomSectionsDTO
import com.vodovoz.app.domain.general.model.CategoryWithProductsModel
import com.vodovoz.app.domain.general.model.SectionModel
import com.vodovoz.app.domain.general.model.TopAndBottomSectionsModel

fun SuperTopAndBottomSectionsDTO.mapToDomain(): TopAndBottomSectionsModel? {
    return TopAndBottomSectionsModel(
        topSection = RAZDEL_VERH?.mapToDomain() ?: return null,
        bottomSection = RAZDEL_NIZ?.mapToDomain() ?: return null
    )
}

fun CATEGORY_WITH_PRODUCTS_DTO.mapToDomain(): CategoryWithProductsModel? {
    return CategoryWithProductsModel(
        id = ID ?: return null,
        products = data?.mapToDomain() ?: return null,
        name = NAME ?: return null
    )
}

fun RAZDEL_VERH_NIH.mapToDomain(): SectionModel {
    return SectionModel(
        name = NAMERAZDEL,
        showAllId = KNOPKA?.ID,
        categoryWithProductsList = DATA?.mapNotNull { it.mapToDomain() } ?: emptyList()
    )
}