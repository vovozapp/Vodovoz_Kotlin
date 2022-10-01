package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.PreOrderFormDataEntity
import com.vodovoz.app.ui.model.PreOrderFormDataUI

object PreOrderFormDataMapper {

    fun PreOrderFormDataEntity.mapToUI() = PreOrderFormDataUI(
        name = name,
        email = email,
        phone = phone
    )

}