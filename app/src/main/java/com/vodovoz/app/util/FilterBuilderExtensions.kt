package com.vodovoz.app.util

import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.util.extensions.debugLog
import java.util.*

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
            this?.let { filterList ->
                filter { it.type == "range_filter" }
                    .forEach {
                        hashMap[it.filterValueList.first().id
                            ?: (it.code.lowercase(Locale.ROOT) + "_from")] =
                            it.filterValueList.first().value
                        hashMap[it.filterValueList.last().id
                            ?: (it.code.lowercase(Locale.ROOT) + "_to")] =
                            it.filterValueList.last().value
                    }
            }
        }
    }

}