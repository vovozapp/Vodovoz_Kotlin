package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.filters.FilterDTO
import com.vodovoz.app.data.vodovoz_service.model.filters.FilterValueDTO
import com.vodovoz.app.data.vodovoz_service.model.filters.FilterValuesDTO
import com.vodovoz.app.data.vodovoz_service.model.filters.FiltersDTO
import com.vodovoz.app.domain.general.model.FilterModel
import com.vodovoz.app.domain.general.model.FilterValueModel
import com.vodovoz.app.domain.general.model.FiltersModel

fun FiltersDTO.toDomain(): FiltersModel {
    val min = CENAFILTER?.MIN ?: 0
    val max = CENAFILTER?.MAX ?: throw IllegalArgumentException("Max of filter price can't be null")
    return FiltersModel(
        priceRange = min..max,
        filters = DANNIE?.mapToDomain() ?: throw IllegalArgumentException("Filters can't be null")
    )
}

fun List<FilterDTO>.mapToDomain(): List<FilterModel> {
    return mapNotNull { it.toDomain() }
}

fun FilterDTO.toDomain(): FilterModel? {
    return FilterModel(
        id = CODE ?: return null,
        name = NAME ?: return null,
        totalValues = ZNACHEIE?.COUNT ?: 0,
        values = ZNACHEIE?.toDomain() ?: emptyList()
    )
}

@JvmName("mapToListFilterValueModel")
fun List<FilterValueDTO>.mapToDomain(): List<FilterValueModel> {
    return mapNotNull {
        FilterValueModel(
            id = it.ID ?: return@mapNotNull null,
            value = it.VALUE ?: return@mapNotNull null
        )
    }
}

fun FilterValuesDTO.toDomain(): List<FilterValueModel>{
    return DATA?.map {
        FilterValueModel(it, it)
    } ?: emptyList()
}