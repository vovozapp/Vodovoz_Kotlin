package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.data.model.common.SortTypeList
import com.vodovoz.app.ui.model.SortTypeListUI
import com.vodovoz.app.ui.model.SortTypeUI

fun SortType.mapToUI() = SortTypeUI(
    sortName, value, orientation)

fun List<SortType>.mapToUI() = mutableListOf<SortTypeUI>().also { uiList ->
    forEach { uiList.add(it.mapToUI()) }
}.toList()

fun SortTypeList.mapToUI() = SortTypeListUI(
    name = name,
    sortTypeList = sortTypeList.mapToUI()
)