package com.vodovoz.app.common.like

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.datastore.DataStoreRepository
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.singleResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikeManager @Inject constructor(
    private val repository: LikeRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val accountManager: AccountManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) {

    companion object {
        private const val FAV_IDS = "fav ids"
    }


    private val mutex = Mutex()
    private val likesVersions = ConcurrentHashMap<Long, Int>()

    private fun getLikeVersion(productId: Long) = likesVersions.getOrDefault(productId, 0)

    private val likes = ConcurrentHashMap<Long, Boolean>()
    private val likesStateListener = MutableSharedFlow<Map<Long, Boolean>>()

    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool().apply {
        setMaxRecycledViews(ProductUI.PRODUCT_VIEW_TYPE, 5)
    }

    fun fetchViewPool() = viewPool

    fun observeLikes() = likesStateListener.asSharedFlow()


    suspend fun changeFavorite(productId: Long, newValue: Boolean) {
        val (likeVersion, userId) = mutex.withLock {
            val updatedVersion = updateFavoritesOptimistically(productId, newValue)
            if (updatedVersion < getLikeVersion(productId)) return

            val userId = accountManager.fetchAccountId()
            updatedVersion to userId
        }


        kotlin.runCatching {
            if (userId != null) {
                updateFavoritesOnline(productId, newValue)
            } else {
                updateFavoritesLocal(productId, newValue)
            }
        }.onFailure {
            mutex.withLock {
                if (likeVersion == getLikeVersion(productId)) {
                    updateFavoritesOptimistically(productId, newValue)
                }
            }
        }
    }

    suspend fun like(productId: Long, isFavorite: Boolean) {
        val (likeVersion, userId) = mutex.withLock {
            val version = updateFavoritesOptimistically(productId, !isFavorite)
            val userId = accountManager.fetchAccountId()
            version to userId
        }


        if (userId != null) {
            runCatching {
                updateFavoritesOnline(productId, isFavorite)
            }.onFailure {
                if (likeVersion >= getLikeVersion(productId)) {
                    updateFavoritesOptimistically(
                        productId,
                        isFavorite
                    )
                }
            }
        } else {
            updateFavoritesLocal(productId, isFavorite)
        }
    }

    private suspend fun updateFavoritesOnline(
        productId: Long,
        newIsFavorite: Boolean,
    ) {
        if (newIsFavorite) {
            vodovozServiceRepository.addProductToFavorites(productId).singleResult().getOrThrow()
        } else {
            vodovozServiceRepository.removeProductFromFavorites(productId).singleResult().getOrThrow()
        }
    }

    private suspend fun updateFavoritesOptimistically(productId: Long, newValue: Boolean): Int {
        if (likes[productId] == newValue) return -1

        val currentVersion = getLikeVersion(productId) + 1
        likesVersions[productId] = currentVersion
        likes[productId] = newValue
        likesStateListener.emit(likes)
        return currentVersion
    }

    private suspend fun updateFavoritesLocal(productId: Long, newIsFavorite: Boolean) {
        val localLikesListString = dataStoreRepository.getString(FAV_IDS)
        val localLikesList = if (localLikesListString.isNullOrEmpty()) {
            listOf(productId)
        } else if (!newIsFavorite) {
            removeInFavoriteStr(localLikesListString, productId)
        } else {
            (parseFavoriteStr(localLikesListString) + listOf(productId))
        }
        dataStoreRepository.putString(FAV_IDS, buildFavoriteStr(localLikesList))
    }

    fun fetchLikeLocalStr(): String? {
        return dataStoreRepository.getString(FAV_IDS)?.dropLast(1)
    }

    private fun buildFavoriteStr(favoriteList: List<Long>): String {
        val favoriteStr = StringBuilder()
        favoriteList.forEach { productId ->
            favoriteStr.append(productId).append(",")
        }
        return favoriteStr.toString()
    }

    private fun parseFavoriteStr(favoriteStr: String): List<Long> {
        val favoriteList = mutableListOf<Long>()
        favoriteStr.split(",").forEach { id ->
            if (id.isNotEmpty()) {
                favoriteList.add(id.toLong())
            }
        }
        return favoriteList.toSet().toList()
    }

    private fun removeInFavoriteStr(favoriteStr: String, productId: Long): List<Long> {
        val favoriteList = mutableListOf<Long>()
        favoriteStr.split(",").forEach { id ->
            if (id.isNotEmpty() && id.toLong() != productId) {
                favoriteList.add(id.toLong())
            }
        }
        return favoriteList.toSet().toList()
    }

    suspend fun syncFavoritesFromLocal() {
        val localLikesListString = dataStoreRepository.getString(FAV_IDS)
        val localLikesList = if (localLikesListString.isNullOrEmpty()) {
            listOf()
        } else {
            parseFavoriteStr(localLikesListString)
        }
        localLikesList.forEach { productId ->
            updateFavoritesOptimistically(productId, true)
        }
    }

    suspend fun updateLikesAfterLogin(userId: Long) {

        val localLikesListString = dataStoreRepository.getString(FAV_IDS)?.dropLast(1) ?: ""

        runCatching {
            vodovozServiceRepository.addFavoriteProducts(localLikesListString).singleOrNull()
            //TODO - delete old repository
            repository.like(productIdListStr = localLikesListString, userId = userId)
            dataStoreRepository.remove(FAV_IDS)
        }
    }

}