package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryDetailUI

object CategoryDetailMapper  {

    fun List<CategoryDetailEntity>.mapToUI() = map { it.mapToUI() }

    fun CategoryDetailEntity.mapToUI() = CategoryDetailUI(
        id = id,
        name = name,
        productAmount = productAmount,
        productUIList = productEntityList.mapToUI()
    )

}