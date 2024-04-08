package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.features.FavoriteProductsHeaderBundleEntity
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.model.custom.FavoriteProductsHeaderBundleUI

object FavoriteProductsHeaderBundleMapper {

    fun FavoriteProductsHeaderBundleEntity.mapToUI() = FavoriteProductsHeaderBundleUI(
        availableTitle = availableTitle,
        notAvailableTitle = notAvailableTitle,
        favoriteCategoryUI = favoriteCategoryEntity?.mapToUI(),
        bestForYouCategoryDetailUI = bestForYouCategoryDetailEntity?.mapToUI(),
        title = title,
        message = message,
    )

}