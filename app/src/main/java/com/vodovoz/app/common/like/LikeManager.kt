package com.vodovoz.app.common.like

import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.data.local.LocalData
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.parser.response.favorite.AddToFavoriteResponseJsonParser.parseAddToFavoriteResponse
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikeManager @Inject constructor(
    private val repository: LikeRepository,
    private val sharedPrefs: SharedPreferences,
    private val accountManager: AccountManager
) {

    companion object {
        private const val FAV_IDS = "fav ids"
    }

    private val likes = ConcurrentHashMap<Long, Boolean>()
    private val likesStateListener = MutableSharedFlow<Map<Long, Boolean>>(replay = 1)

    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool().apply {
        setMaxRecycledViews(ProductUI.PRODUCT_VIEW_TYPE, 5)
    }
    fun fetchViewPool() = viewPool

    fun observeLikes() = likesStateListener.asSharedFlow().filter { it.isNotEmpty() }

    suspend fun like(id: Long, isFavorite: Boolean) {
        updateLikes(id, !isFavorite)

        val userId = accountManager.fetchAccountId()

        if (userId != null) {
            runCatching {
                action(id, userId, isFavorite)
            }.onFailure {
                updateLikes(id, isFavorite)
            }
        } else {
            saveLikeLocal(id, isFavorite)
        }
    }

    private suspend fun action(productId: Long, userId: Long, isFavorite: Boolean) {
        return if (isFavorite) {
            repository.dislike(productId, userId)
            updateLikes(productId, false)
        } else {
            repository.like(listOf(productId), userId)
            updateLikes(productId, true)
        }
    }

    private suspend fun updateLikes(id: Long, state: Boolean) {
        likes[id] = state
        likesStateListener.emit(likes)
    }

    private suspend fun saveLikeLocal(productId: Long, isFavorite: Boolean) {
        val localLikesListString = sharedPrefs.getString(FAV_IDS, "")
        val localLikesList = if (localLikesListString.isNullOrEmpty()) {
            listOf(productId)
        } else {
            if (isFavorite) {
                removeInFavoriteStr(localLikesListString, productId)
            } else {
                parseFavoriteStr(localLikesListString) + listOf(productId)
            }
        }
        sharedPrefs.edit().putString(FAV_IDS, buildFavoriteStr(localLikesList)).apply()
        delay(1000)
        updateLikes(productId, !isFavorite)
    }

    fun fetchLikeLocalStr() : String? {
        return sharedPrefs.getString(FAV_IDS, "")?.dropLast(1)
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

    suspend fun updateStateFromLikesLocal() {
        val localLikesListString = sharedPrefs.getString(FAV_IDS, "")
        val localLikesList = if (localLikesListString.isNullOrEmpty()) {
            listOf()
        } else {
            parseFavoriteStr(localLikesListString)
        }
        localLikesList.forEach {
            updateLikes(it, true)
        }
    }

    suspend fun updateLikesAfterLogin(userId: Long) {

        val localLikesListString = sharedPrefs.getString(FAV_IDS, "")?.dropLast(1) ?: ""

        kotlin.runCatching {
            val resp = repository.like(productIdListStr = localLikesListString, userId = userId)
            sharedPrefs.edit().remove(FAV_IDS).apply()
        }
    }

}