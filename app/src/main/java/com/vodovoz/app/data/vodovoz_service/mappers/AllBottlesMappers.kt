package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.AllBottlesDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.TARA_DTO
import com.vodovoz.app.domain.general.AllBottlesDetailsModel
import com.vodovoz.app.domain.general.BottleModel

fun AllBottlesDetailsDTO.toDomain(): AllBottlesDetailsModel{
    return AllBottlesDetailsModel(
        description = OPISANIE ?: "",
        isSingleBottleMode = KPOPKAPLUS != "Y",
        bottles = TARA?.mapToDomain() ?: emptyList()
    )
}

fun TARA_DTO.toDomain(): BottleModel?{
    return BottleModel(
        name = NAME ?: "",
        id = ID ?: return null,
        articleText = PROPERTY_CML2_ARTICLE_VALUE ?: "",
        description = OPISANIE ?: "",
        cartQuantity = 0
    )
}

fun List<TARA_DTO>.mapToDomain(): List<BottleModel>{
    return mapNotNull { it.toDomain() }
}