package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.ui.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryDetailUI

object CategoryDetailMapper  {

    fun List<CategoryDetailEntity>.mapToUI(): List<CategoryDetailUI> = mutableListOf<CategoryDetailUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun CategoryDetailEntity.mapToUI() = CategoryDetailUI(
        id = id,
        name = name,
        productAmount = productAmount,
        productUIList = productEntityList.mapToUI()
    )

}