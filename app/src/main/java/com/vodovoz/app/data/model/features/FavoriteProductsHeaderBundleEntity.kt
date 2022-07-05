package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.CategoryEntity

class FavoriteProductsHeaderBundleEntity(
    val availableTitle: String? = null,
    val notAvailableTitle: String? = null,
    val favoriteCategoryEntity: CategoryEntity? = null,
    val bestForYouCategoryDetailEntity: CategoryDetailEntity? = null
)