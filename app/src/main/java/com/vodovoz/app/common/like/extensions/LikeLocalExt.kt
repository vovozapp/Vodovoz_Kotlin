package com.vodovoz.app.common.like.extensions

import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ProductDetailEntity
import com.vodovoz.app.data.model.common.ProductEntity

fun List<ProductEntity>?.syncFavoriteProducts(localDataSource: LocalDataSource) {
    val favoriteList = localDataSource.fetchAllFavoriteProducts()
    this?.map { productEntity -> productEntity.apply {
        favoriteList.find { it == productEntity.id }?.let {
            productEntity.isFavorite = true
        }
    } }
}

fun ProductDetailEntity?.syncFavoriteStatus(localDataSource: LocalDataSource) {
    val favoriteList = localDataSource.fetchAllFavoriteProducts()
    favoriteList.find { it == this?.id }?.let {
        this?.isFavorite = true
    }
}