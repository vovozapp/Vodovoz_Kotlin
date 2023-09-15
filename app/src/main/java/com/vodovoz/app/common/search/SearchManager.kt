package com.vodovoz.app.common.search

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchManager @Inject constructor(
    private val sharedPrefs: SharedPreferences,
) {

    companion object {
        private const val SEARCH_SETTINGS = "search_settings"
        private const val SEARCH_HISTORY = "SEARCH_HISTORY"
    }


    fun clearSearchHistory() {
        sharedPrefs.edit().remove(SEARCH_HISTORY).apply()
    }

    fun addQueryToHistory(query: String) {
        if (query.isNotEmpty()) {
            val queryList = fetchSearchHistory().toMutableList()
            val cont = queryList.find { it == query }
            if (cont == null) {
                queryList.add(query)
                sharedPrefs.edit().putString(SEARCH_HISTORY, buildSearchHistoryStr(queryList))
                    .apply()
            }
        }
    }

    fun fetchSearchHistory() =
        parseSearchHistoryStr(sharedPrefs.getString(SEARCH_HISTORY, "") ?: "")

    private fun parseSearchHistoryStr(searchHistoryStr: String): List<String> {
        val queryList = searchHistoryStr.split(",").toMutableList()
        return queryList.filter { it.isNotEmpty() }
    }

    private fun buildSearchHistoryStr(queryList: List<String>) = StringBuilder().apply {
        queryList.forEach { query ->
            append(query).append(",")
        }
    }.toString()
}