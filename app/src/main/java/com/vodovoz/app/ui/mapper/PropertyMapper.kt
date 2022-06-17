package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.PropertyEntity
import com.vodovoz.app.ui.model.PropertyUI

object PropertyMapper  {

    fun List<PropertyEntity>.mapToUI(): List<PropertyUI> = mutableListOf<PropertyUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun PropertyEntity.mapToUI() = PropertyUI(
        name = name,
        value = value
    )

}