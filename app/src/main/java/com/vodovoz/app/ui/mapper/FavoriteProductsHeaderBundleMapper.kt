package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.features.FavoriteProductsHeaderBundleEntity
import com.vodovoz.app.ui.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.ui.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.model.custom.FavoriteProductsHeaderBundleUI

object FavoriteProductsHeaderBundleMapper {

    fun FavoriteProductsHeaderBundleEntity.mapToUI() = FavoriteProductsHeaderBundleUI(
        availableTitle = availableTitle,
        notAvailableTitle = notAvailableTitle,
        favoriteCategoryUI = favoriteCategoryEntity?.mapToUI(),
        bestForYouCategoryDetailUI = bestForYouCategoryDetailEntity?.mapToUI()
    )

}