package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.ui.mapper.FilterValueMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryUI

object CategoryMapper  {

    fun List<CategoryEntity>.mapToUI(): List<CategoryUI> = mutableListOf<CategoryUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun CategoryEntity.mapToUI() = CategoryUI(
        id = id,
        name = name,
        detailPicture = detailPicture,
        productAmount = productAmount,
        primaryFilterName = primaryFilterName,
        primaryFilterValueList = primaryFilterValueList.mapToUI(),
        categoryUIList = subCategoryEntityList.mapToUI()
    )

}