package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.ServiceEntity
import com.vodovoz.app.ui.model.ServiceUI

object ServiceMapper {

    fun List<ServiceEntity>.mapToUI(): List<ServiceUI> = mutableListOf<ServiceUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun ServiceEntity.mapToUI() = ServiceUI(
        name = name,
        price = price,
        detail = detail,
        detailPicture = detailPicture,
        type = type
    )

}