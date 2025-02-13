package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.CategoryEntity

class PastPurchasesHeaderBundleEntity(
    val availableTitle: String? = null,
    val notAvailableTitle: String? = null,
    val favoriteCategoryEntity: CategoryEntity? = null,
    val emptyTitle: String = "",
    val emptySubtitle: String = ""
)