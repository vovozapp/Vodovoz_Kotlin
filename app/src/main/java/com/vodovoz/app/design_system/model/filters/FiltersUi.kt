package com.vodovoz.app.design_system.model.filters

import com.vodovoz.app.domain.general.model.FiltersModel

data class FiltersUi(
    val price: FiltersPriceUi,
    val filters: List<FilterUi>,
) {
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
