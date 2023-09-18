package com.vodovoz.app.common.like.data

import android.content.SharedPreferences
import javax.inject.Inject

class LikeLocal @Inject constructor(
    private val sharedPrefs: SharedPreferences
) {

    fun changeFavoriteStatus(pairList: List<Pair<Long, Boolean>>) {
        val favoriteList = fetchAllFavoriteProducts().toMutableList()

        pairList.forEach { pair ->
            when (pair.second) {
                true -> favoriteList.add(pair.first)
                else -> favoriteList.remove(pair.first)
            }
        }

        sharedPrefs.edit()
            .putString(DEFAULT_USER_FAVORITE_LIST, buildFavoriteStr(favoriteList))
            .apply()

    }

    private fun fetchAllFavoriteProducts() =
        parseFavoriteStr(sharedPrefs.getString(DEFAULT_USER_FAVORITE_LIST, ""))

    private fun parseFavoriteStr(favoriteStr: String?): List<Long> {
        if (favoriteStr == null) return emptyList()

        val favoriteList = mutableListOf<Long>()
        favoriteStr.split(",").forEach { id ->
            favoriteList.add(id.toLong())
        }
        return favoriteList.toList()
    }

    private fun buildFavoriteStr(favoriteList: List<Long>): String {
        val favoriteStr = StringBuilder()
        favoriteList.forEach { productId ->
            favoriteStr.append(productId).append(",")
        }
        return favoriteStr.toString()
    }

    companion object {
        private const val DEFAULT_USER_FAVORITE_LIST = "DEFAULT_USER_FAVORITE_LIST"
    }
}