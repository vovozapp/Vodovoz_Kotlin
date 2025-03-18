package com.vodovoz.app.design_system.model.filters

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.FilterModel

@Immutable
data class FilterUi(
    val id: String,
    val name: String,
    val values: List<FilterValueUi>,
)

fun FilterModel.toUi(): FilterUi {
    return FilterUi(
        id = id,
        name = name,
        values = values.mapToUi()
    )
}

fun List<FilterModel>.mapToUi(): List<FilterUi> {
    return map { it.toUi() }
}