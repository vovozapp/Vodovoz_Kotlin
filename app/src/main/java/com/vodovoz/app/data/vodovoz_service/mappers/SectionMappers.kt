package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.CATEGORY_WITH_PRODUCTS_DTO
import com.vodovoz.app.data.vodovoz_service.model.KNOPKA_DTO
import com.vodovoz.app.data.vodovoz_service.model.KNOPKA_INT_DTO
import com.vodovoz.app.data.vodovoz_service.model.RAZDEL_VERH_NIH
import com.vodovoz.app.data.vodovoz_service.model.SuperTopAndBottomSectionsDTO
import com.vodovoz.app.domain.general.model.ButtonModel
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

fun RAZDEL_VERH_NIH.mapToDomain(): SectionModel<CategoryWithProductsModel> {
    return SectionModel(
        title = NAMERAZDEL ?: "",
        button = KNOPKA?.mapToDomain(),
        items = DATA?.mapNotNull { it.mapToDomain() } ?: emptyList()
    )
}

fun KNOPKA_DTO.mapToDomain(): ButtonModel? {
    return ButtonModel(
        name = NAME ?: "",
        id = ID ?: return null
    )
}

fun KNOPKA_INT_DTO.mapToDomain(): ButtonModel? {
    return ButtonModel(
        name = NAME ?: "",
        id = ID?.toString() ?: return null
    )
}