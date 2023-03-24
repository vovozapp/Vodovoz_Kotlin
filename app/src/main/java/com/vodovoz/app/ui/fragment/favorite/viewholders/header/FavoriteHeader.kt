package com.vodovoz.app.ui.fragment.favorite.viewholders.header

import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.CategoryUI

data class FavoriteHeader(
    val id: Int,
    val favoriteCategory: CategoryUI? = null,
    val bestForYouCategoryDetailUI: CategoryDetailUI? = null,
    val availableTitle: String? = null,
    val notAvailableTitle: String? = null
)
