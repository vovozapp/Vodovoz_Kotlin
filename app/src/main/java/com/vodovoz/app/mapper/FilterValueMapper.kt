package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.FilterValueEntity
import com.vodovoz.app.ui.model.FilterValueUI


fun FilterValueEntity.mapToUI() = FilterValueUI(
    id = id,
    value = if(value.contains("&lt;")) {
                value.replaceFirst("&lt;", "<")
            } else {
                value
            }
)

fun List<FilterValueEntity>.mapToUI() = mutableListOf<FilterValueUI>().also { uiList ->
    forEach { uiList.add(it.mapToUI()) }
}.toList()
