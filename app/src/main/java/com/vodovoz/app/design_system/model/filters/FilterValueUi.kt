package com.vodovoz.app.design_system.model.filters

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.FilterValueModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class FilterValueUi(
    val id: String,
    val name: String,
    val selected: Boolean = false,
) : Parcelable

fun FilterValueModel.toUi(): FilterValueUi {
    return FilterValueUi(
        id = id,
        name = value
    )
}

fun List<FilterValueModel>.mapToUi(): List<FilterValueUi> {
    return map {
        it.toUi()
    }
}


fun FilterValueUi.toDomain(): FilterValueModel {
    return FilterValueModel(
        id = id,
        value = name
    )
}

fun List<FilterValueUi>.mapToDomain(): List<FilterValueModel> {
    return map { it -> it.toDomain() }
}

