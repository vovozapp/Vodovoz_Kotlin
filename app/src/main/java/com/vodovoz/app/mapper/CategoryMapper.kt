package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.ui.model.CategoryUI

object CategoryMapper {

    fun List<CategoryEntity>.mapToUI(): List<CategoryUI> =
        mutableListOf<CategoryUI>().also { uiList ->
            forEach { uiList.add(it.mapToUI()) }
        }

    fun CategoryEntity.mapToUI() = CategoryUI(
        id = id,
        name = name,
        shareUrl = shareUrl,
        detailPicture = detailPicture,
        productAmount = productAmount,
        primaryFilterName = primaryFilterName,
        primaryFilterValueList = primaryFilterValueList.mapToUI(),
        categoryUIList = subCategoryEntityList.mapToUI(),
        filterCode = filterCode
    )

}