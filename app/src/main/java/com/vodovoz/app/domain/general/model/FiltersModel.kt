package com.vodovoz.app.domain.general.model

data class FiltersModel(
    val priceRange: IntRange,
    val filters: List<FilterModel>,
)

data class FilterModel(
    val id: String,
    val name: String,
    val totalValues: Int,
    val values: List<FilterValueModel>,
)

data class FilterValueModel(
    val id: String,
    val value: String,
)

fun List<FilterModel>.format(): String {
    return joinToString(";") { filter ->
        "${filter.id}@${filter.values.joinToString(",") { it.id }}"
    }
}