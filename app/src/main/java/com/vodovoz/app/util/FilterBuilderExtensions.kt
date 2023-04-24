package com.vodovoz.app.util

import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.util.extensions.debugLog

object FilterBuilderExtensions {

    fun List<FilterUI>?.buildFilterQuery(): String {
        return try {
            StringBuilder().also { builder ->
                this?.let {
                    forEach { builder.append(it.code).append(",") }
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
        } catch (t: Throwable) {
            debugLog { "filter builder ext $t" }
            ""
        }
    }

}