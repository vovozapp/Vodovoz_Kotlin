package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.PopupNewsEntity
import com.vodovoz.app.ui.model.PopupNewsUI

object PopupNewsMapper {

    fun PopupNewsEntity.mapToUI() = PopupNewsUI(
        name = name,
        detailText = detailText,
        detailPicture = detailPicture,
        actionEntity = actionEntity
    )

}