package com.vodovoz.app.util

import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.util.extensions.debugLog
import java.util.Locale

object FilterBuilderExtensions {

    fun List<FilterUI>?.buildFilterQuery(): String {
        return try {
            StringBuilder().also { builder ->
                this?.let {
                    filter { it.type != "range_filter" }
                        .forEach { builder.append(it.code).append(",") }
                }
            }.toString()
        } catch (t: Throwable) {
            debugLog { "filter builder ext $t" }
            ""
        }
    }

    fun List<FilterUI>?.buildFilterValueQuery(): String {
        return try {
            StringBuilder().also { builder ->
                this?.let {
                    filter { it.type != "range_filter" }
                        .forEach { filter ->
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
        } catch (t: Throwable) {
            debugLog { "filter builder ext $t" }
            ""
        }
    }

    fun List<FilterUI>?.buildFilterRangeQuery(): HashMap<String, String> {
        return hashMapOf<String, String>().also { hashMap ->
            this?.let {
                filter {filter ->
                    filter.type == "range_filter"
                }
                    .forEach {filter->
                        hashMap[filter.filterValueList.first().id
                            ?: (filter.code.lowercase(Locale.ROOT) + "_from")] =
                            filter.filterValueList.first().value
                        hashMap[filter.filterValueList.last().id
                            ?: (filter.code.lowercase(Locale.ROOT) + "_to")] =
                            filter.filterValueList.last().value
                    }
            }
        }
    }

}