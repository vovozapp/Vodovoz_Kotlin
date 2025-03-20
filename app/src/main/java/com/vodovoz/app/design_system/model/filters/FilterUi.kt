package com.vodovoz.app.design_system.model.filters

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.FilterModel
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class FilterUi(
    val id: String,
    val name: String,
    val totalValues: Int,
    val values: List<FilterValueUi>,
): Parcelable {
    companion object {
        val Empty = FilterUi("", "", 0, emptyList())
    }
}

fun FilterModel.toUi(): FilterUi {
    return FilterUi(
        id = id,
        name = name,
        totalValues = totalValues,
        values = values.mapToUi()
    )
}

fun List<FilterModel>.mapToUi(): List<FilterUi> {
    return map { it.toUi() }
}

fun FilterUi.toDomain(): FilterModel {
    return FilterModel(
        id = id,
        name = name,
        totalValues = totalValues,
        values = values.mapToDomain()
    )
}

fun List<FilterUi>.mapToDomain(): List<FilterModel> {
    return map { it.toDomain() }
}