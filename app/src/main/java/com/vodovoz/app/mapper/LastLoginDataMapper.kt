package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.LastLoginDataEntity
import com.vodovoz.app.ui.model.LastLoginDataUI

object LastLoginDataMapper {

    fun LastLoginDataEntity.mapToUI() = LastLoginDataUI(
        email = email,
        password = password
    )

    fun LastLoginDataUI.mapToEntity() = LastLoginDataEntity(
        email = email,
        password = password
    )

}