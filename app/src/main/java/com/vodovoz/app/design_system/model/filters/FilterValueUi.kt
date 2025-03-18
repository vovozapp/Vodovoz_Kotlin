package com.vodovoz.app.design_system.model.filters

import com.vodovoz.app.domain.general.model.FilterValueModel

data class FilterValueUi(
    val id: String,
    val name: String
)

fun FilterValueModel.toUi(): FilterValueUi{
    return FilterValueUi(
        id = id,
        name = value
    )
}

fun List<FilterValueModel>.mapToUi(): List<FilterValueUi>{
    return map {
        it.toUi()
    }
}
