package com.vodovoz.app.util

import com.vodovoz.app.ui.model.FilterUI

object FilterBuilderExtensions {

    fun List<FilterUI>?.buildFilterQuery() = StringBuilder().also { builder ->
        this?.let {
            forEach { builder.append(it.code).append(",") }
        }
    }.toString()

    fun List<FilterUI>?.buildFilterValueQuery() = StringBuilder().also { builder ->
        this?.let {
            forEach { filter ->
                builder.append(filter.code).append("@")
                filter.filterValueList.forEach { filterValue ->
                    builder.append(filterValue.id)
                    if (filterValue != filter.filterValueList.last()) {
                        builder.append(",")
                    }
                }
                builder.append(";")
            }
        }
    }.toString()

}