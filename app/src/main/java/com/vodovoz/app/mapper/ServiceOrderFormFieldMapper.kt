package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.ServiceOrderFormFieldEntity
import com.vodovoz.app.ui.model.ServiceOrderFormFieldUI

object ServiceOrderFormFieldMapper {

    fun List<ServiceOrderFormFieldEntity>.mapToUI(): List<ServiceOrderFormFieldUI> =
        mutableListOf<ServiceOrderFormFieldUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun ServiceOrderFormFieldEntity.mapToUI() = ServiceOrderFormFieldUI(
        id = id,
        name = name,
        defaultValue = defaultValue,
        isRequired = isRequired
    )

}