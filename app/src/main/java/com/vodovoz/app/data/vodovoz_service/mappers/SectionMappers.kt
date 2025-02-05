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

fun SuperTopAndBottomSectionsDTO.toDomain(): TopAndBottomSectionsModel? {
    return TopAndBottomSectionsModel(
        topSection = RAZDEL_VERH?.toDomain() ?: return null,
        bottomSection = RAZDEL_NIZ?.toDomain() ?: return null
    )
}

fun CATEGORY_WITH_PRODUCTS_DTO.toDomain(): CategoryWithProductsModel? {
    return CategoryWithProductsModel(
        id = ID ?: return null,
        products = data?.toDomain() ?: return null,
        name = NAME ?: return null
    )
}

fun CATEGORY_RAZDEL.toDomain(): SectionModel<CategoryWithProductsModel> {
    val categoriesWithProducts = DATA?.mapNotNull { it.toDomain() } ?: emptyList()
    return SectionModel(
        title = NAMERAZDEL ?: categoriesWithProducts.firstOrNull { it.name.isNotEmpty() }?.name
        ?: "",
        button = KNOPKA?.toDomain(),
        items = categoriesWithProducts
    )
}

fun RAZDEL_DTO.toDomain(): SectionModel<ProductModel> {
    return SectionModel(
        title = TITLE ?: "",
        button = KNOPKA?.toDomain(),
        items = DATA?.mapNotNull { tovarDataDto -> tovarDataDto?.toDomain() } ?: emptyList()
    )
}

fun KNOPKA_DTO.toDomain(): ButtonModel? {
    return ButtonModel(
        name = NAME ?: "",
        action = ID?.toButtonAction() ?: return null
    )
}

fun KNOPKA_INT_DTO.toDomain(): ButtonModel? {
    return ButtonModel(
        name = NAME ?: "",
        action = ButtonInfo.Id(ID ?: return null)
    )
}

private fun String.toButtonAction(): ButtonInfo {
    val id = toIntOrNull()
    return if (id != null) {
        ButtonInfo.Id(id)
    } else {
        ButtonInfo.Action(toDataAll())
    }
}
