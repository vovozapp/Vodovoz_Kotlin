package com.vodovoz.app.data

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ProductDetailEntity
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.util.LogSettings

object LocalSyncExtensions {

    fun PagingData<ProductEntity>?.syncFavoriteProducts(localDataSource: LocalDataSource) {
        val favoriteList = localDataSource.fetchAllFavoriteProducts()
        this?.map { productEntity -> productEntity.apply {
            favoriteList.find { it == productEntity.id }?.let {
                productEntity.isFavorite = true
            }
        } }
    }

    fun PagingData<ProductEntity>?.syncCartQuantity(localDataSource: LocalDataSource) {
        val localCart = localDataSource.fetchCart()
        this?.map { productEntity ->
            localCart[productEntity.id]?.let { localQuantity ->
                productEntity.cartQuantity = localQuantity
            }
            productEntity
        }
    }

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

    fun List<ProductEntity>?.syncCartQuantity(localDataSource: LocalDataSource) {
        val localCart = localDataSource.fetchCart()
        this?.map { productEntity -> productEntity.apply {
            localCart[productEntity.id]?.let { localQuantity -> productEntity.cartQuantity = localQuantity}
        } }
    }

    fun ProductDetailEntity.syncCartQuantity(localDataSource: LocalDataSource) {
        val localCart = localDataSource.fetchCart()
        localCart[id]?.let { localQuantity -> cartQuantity = localQuantity}
    }


}