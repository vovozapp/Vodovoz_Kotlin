package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.BrandEntity
import com.vodovoz.app.ui.model.BrandUI

object BrandMapper {

    fun List<BrandEntity>.mapToUI() = map { it.mapToUI() }

    fun BrandEntity.mapToUI() = BrandUI(
        id = id,
        name = name,
        detailPicture = detailPicture,
        hasList = hasList
    )

}