package com.vodovoz.app.feature.favorite.mapper

import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel.Companion.GRID
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel.Companion.LINEAR
import com.vodovoz.app.ui.model.ProductUI

object FavoritesMapper {

    fun mapFavoritesListByManager(manager: String, list: List<ProductUI>) : List<ProductUI> {
        return when(manager) {
            LINEAR -> {
                list.map { it.copy(linear = true) }
            }
            GRID -> {
                list.map { it.copy(linear = false) }
            }
            else -> emptyList()
        }
    }
}