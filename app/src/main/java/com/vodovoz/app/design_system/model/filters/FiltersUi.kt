package com.vodovoz.app.design_system.model.filters

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.FiltersModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class FiltersUi(
    val price: FiltersPriceUi,
    val filters: List<FilterUi>,
): Parcelable {
    companion object {
        val Empty = FiltersUi(price = FiltersPriceUi.Empty, filters = emptyList())
    }
}

fun FiltersModel.toUi(): FiltersUi {
    return FiltersUi(
        price = FiltersPriceUi(priceRange.first, priceRange.last),
        filters = filters.mapToUi()
    )
}

fun FiltersUi.toDomain(): FiltersModel {
    return FiltersModel(
        priceRange = price.currentMin..price.currentMax,
        filters = filters.mapToDomain()
    )
}
