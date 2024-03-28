package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.CatalogBanner
import com.vodovoz.app.data.model.common.CatalogEntity
import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.ui.model.CatalogBannerUI
import com.vodovoz.app.ui.model.CatalogUI
import com.vodovoz.app.ui.model.CategoryUI

object CategoryMapper {

    fun CatalogEntity.mapToUI(): CatalogUI {
        return CatalogUI(
            categoryEntityList = categoryEntityList.mapToUI(),
            topCatalogBanner = topCatalogBanner?.mapToUI()
        )
    }

    fun CatalogBanner.mapToUI(): CatalogBannerUI = CatalogBannerUI(
        text = text,
        textColor = textColor,
        backgroundColor = backgroundColor,
        iconUrl = iconUrl,
        actionEntity = actionEntity
    )

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
        filterCode = filterCode,
        sortTypeList = sortTypeList?.mapToUI(),
        actionEntity = actionEntity
    )

}