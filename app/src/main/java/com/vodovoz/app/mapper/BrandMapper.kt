package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.BrandEntity
import com.vodovoz.app.ui.model.BrandUI

object BrandMapper {

    fun List<BrandEntity>.mapToUI(): List<BrandUI> = mutableListOf<BrandUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun BrandEntity.mapToUI() = BrandUI(
        id = id,
        name = name,
        detailPicture = detailPicture
    )

}