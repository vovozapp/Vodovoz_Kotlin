package com.vodovoz.app.data.model.common

enum class SortType(
    val sortName: String,
    val value: String,
    val orientation: String
) {
    ALPHABET("По алфавиту", "name", "asc"),
    POPULAR("По популярности", "popylyar", "desc"),
    INCREASE_PRICE("По возрастанию цены", "price", "asc"),
    REDUCE_PRICE("По убыванию цены", "price", "desc"),
    NO_SORT("Выбрать сортировку", "", "")
}