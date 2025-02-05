package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.CATEGORY_RAZDEL
import com.vodovoz.app.data.vodovoz_service.model.CATEGORY_WITH_PRODUCTS_DTO
import com.vodovoz.app.data.vodovoz_service.model.KNOPKA_DTO
import com.vodovoz.app.data.vodovoz_service.model.KNOPKA_INT_DTO
import com.vodovoz.app.data.vodovoz_service.model.RAZDEL_DTO
import com.vodovoz.app.data.vodovoz_service.model.SuperTopAndBottomSectionsDTO
import com.vodovoz.app.domain.general.model.ButtonInfo
import com.vodovoz.app.domain.general.model.ButtonModel
import com.vodovoz.app.domain.general.model.CategoryWithProductsModel
import com.vodovoz.app.domain.general.model.ProductModel
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

fun CATEGORY_RAZDEL.mapToDomain(): SectionModel<CategoryWithProductsModel> {
    val categoriesWithProducts = DATA?.mapNotNull { it.mapToDomain() } ?: emptyList()
    return SectionModel(
        title = NAMERAZDEL ?: categoriesWithProducts.firstOrNull { it.name.isNotEmpty() }?.name
        ?: "",
        button = KNOPKA?.mapToDomain(),
        items = categoriesWithProducts
    )
}

fun RAZDEL_DTO.mapToDomain(): SectionModel<ProductModel> {
    return SectionModel(
        title = TITLE ?: "",
        button = KNOPKA?.mapToDomain(),
        items = DATA?.mapNotNull { tovarDataDto -> tovarDataDto?.mapToDomain() } ?: emptyList()
    )
}

fun KNOPKA_DTO.mapToDomain(): ButtonModel? {
    return ButtonModel(
        name = NAME ?: "",
        action = ID?.mapToButtonAction() ?: return null
    )
}

fun KNOPKA_INT_DTO.mapToDomain(): ButtonModel? {
    return ButtonModel(
        name = NAME ?: "",
        action = ButtonInfo.Id(ID ?: return null)
    )
}

private fun String.mapToButtonAction(): ButtonInfo {
    val id = toIntOrNull()
    return if (id != null) {
        ButtonInfo.Id(id)
    } else {
        ButtonInfo.Action(mapToDataAll())
    }
}
